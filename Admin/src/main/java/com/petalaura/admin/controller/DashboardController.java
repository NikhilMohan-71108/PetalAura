package com.petalaura.admin.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.DashBoardService;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.DailyEarningMapping;
import com.petalaura.library.dto.MonthlyEarnMap;
import com.petalaura.library.dto.TotalPriceByPayment;
import com.petalaura.library.utils.ReportGenerator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

@Controller
public class DashboardController {
    @Autowired
    DashBoardService dashBoardService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    OrderService orderService;

    @RequestMapping(value = {"/"})
    public String home(Model model, Principal principal,
                       HttpSession session,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                       @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws JsonProcessingException {
        if (principal == null) {
            return "redirect:/loginPage";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Home Page");

        addEarningData(model);
        addOrderData(model);
        addProductAndCategoryData(model);
        addEarningChartData(model);
        addPieChartData(model);

        setFilteredData(model, filter, startDate, endDate);


        List<Object[]> topProducts =  productService.findTopSellingProductsDashBoard(PageRequest.of(0, 10));
        List<Object[]> topCategories = categoryService.findTopSellingCategories(PageRequest.of(0, 5));

        model.addAttribute("topProducts", topProducts);
        model.addAttribute("topCategories", topCategories);

        session.setAttribute("userLoggedIn", true);
        return "index";
    }

    private void addEarningData(Model model) {
        YearMonth currentYearMonth = YearMonth.now();
        Date startDate = java.sql.Date.valueOf(currentYearMonth.atDay(1));
        Date endDate = java.sql.Date.valueOf(currentYearMonth.atEndOfMonth());

        double currentMonthEarning = dashBoardService.findCurrentMonthOrder(startDate, endDate);
        model.addAttribute("currentMonth", currentYearMonth.getMonth());
        model.addAttribute("currentMonthEarning", currentMonthEarning);

        Date startDateYearly = java.sql.Date.valueOf(LocalDate.of(currentYearMonth.getYear(), Month.JANUARY, 1));
        Date endDateYearly = java.sql.Date.valueOf(LocalDate.of(currentYearMonth.getYear(), Month.DECEMBER, 31));
        double currentYearlyEarning = dashBoardService.findCurrentMonthOrder(startDateYearly, endDateYearly);

        model.addAttribute("currentYear", currentYearMonth.getYear());
        model.addAttribute("currentYearlyEarnings", currentYearlyEarning);
    }

    private void addOrderData(Model model) {
        int totalOrders = (int) dashBoardService.findOrdersTotal();
        model.addAttribute("totalOrders", totalOrders);

        int totalPendingOrders = (int) dashBoardService.findOrdersPending();
        model.addAttribute("totalPendingOrders", totalPendingOrders);

        int progress = totalOrders != 0 ? (totalPendingOrders * 100) / totalOrders : 0;
        model.addAttribute("progress", progress);
    }

    private void addProductAndCategoryData(Model model) {
        long totalProducts = productService.countTotalProducts();
        long totalCategories = categoryService.countTotalCategories();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
    }

    private void addEarningChartData(Model model) throws JsonProcessingException {
        YearMonth currentYearMonth = YearMonth.now();
        List<DailyEarningMapping> dailyEarningListForJson = mapToDailyEarningMappings(dashBoardService.retrieveDailyEarnings(currentYearMonth.getYear(), currentYearMonth.getMonthValue()));
        List<MonthlyEarnMap> monthlyEarnJson = mapToMonthlyEarnMaps(dashBoardService.retriveMontlyEarning(currentYearMonth.getYear()));

        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("dailyEarnings", objectMapper.writeValueAsString(dailyEarningListForJson));
        model.addAttribute("monthlyEarn", objectMapper.writeValueAsString(monthlyEarnJson));
    }
    private List<MonthlyEarnMap> mapToMonthlyEarnMaps(List<Object[]> monthlyEarnings) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return monthlyEarnings.stream()
                .map(objects -> {
                    try {
                        String datePart = dateFormat.format((Date) objects[0]);
                        Double earning = (Double) objects[1];
                        return new MonthlyEarnMap(datePart, earning);
                    } catch (Exception e) {
                        // Log the error and handle it gracefully
                        System.err.println("Error mapping monthly earnings: " + e.getMessage());
                        return null; // or a default value, depending on your requirement
                    }
                })
                .filter(Objects::nonNull) // Exclude null values if any errors occurred
                .toList();
    }
    private void addPieChartData(Model model) throws JsonProcessingException {
        List<TotalPriceByPayment> totalPriceByPaymentList = mapToTotalPriceByPayments(dashBoardService.findTotalPricesByPayment());
        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("totalPriceByPayment", objectMapper.writeValueAsString(totalPriceByPaymentList));

    }

    private void setFilteredData(Model model, String filter, Date startDate, Date endDate) {
        if (!filter.isEmpty()) {
            DateRange dateRange = getDateRange(filter);
            startDate = dateRange.getStartDate();
            endDate = dateRange.getEndDate();
        }

        List<Object[]> productStats = filter.isEmpty() ? productService.getProductStats() : productService.getProductsStatsBetweenDates(startDate, endDate);
        model.addAttribute("productStats", productStats);

        model.addAttribute("TotalAmount", orderService.getTotalOrderAmount());
        model.addAttribute("TotalProducts", productService.countTotalProducts());
        model.addAttribute("TotalCategory", categoryService.countTotalCategories());
        model.addAttribute("TotalOrders", dashBoardService.findOrdersTotal());
        model.addAttribute("MonthlyRevenue", orderService.getTotalAmountForMonth());
    }

    private DateRange getDateRange(String filter) {
        Calendar calendar = Calendar.getInstance();
        Date startDate;
        Date endDate = new Date();

        switch (filter) {
            case "week":
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                startDate = calendar.getTime();
                break;
            case "month":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();
                break;
            case "day":
                LocalDate today = LocalDate.now();
                startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                endDate = Date.from(today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "year":
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                startDate = calendar.getTime();
                break;
            default:
                startDate = null;
                endDate = null;
                break;
        }
        return new DateRange(startDate, endDate);
    }

    private List<DailyEarningMapping> mapToDailyEarningMappings(List<Object[]> dailyEarnings) {
        List<DailyEarningMapping> dailyEarningList = new ArrayList<>();
        for (Object[] obj : dailyEarnings) {
            String datePart = new SimpleDateFormat("yyyy-MM-dd").format((Date) obj[0]);
            dailyEarningList.add(new DailyEarningMapping(datePart, (Double) obj[1]));
        }
        return dailyEarningList;
    }



    private List<TotalPriceByPayment> mapToTotalPriceByPayments(List<Object[]> priceByPayMethod) {
        List<TotalPriceByPayment> totalPriceByPaymentList = new ArrayList<>();
        for (Object[] obj : priceByPayMethod) {
            totalPriceByPaymentList.add(new TotalPriceByPayment((String) obj[0], (Double) obj[1]));
        }
        return totalPriceByPaymentList;
    }

    @PostMapping("/generateReport")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> salesReportGenerator(@RequestBody Map<String, Object> requestData) throws ParseException, IOException {
        String type = (String) requestData.get("type");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = dateFormat.parse((String) requestData.get("from"));
        Date toDate = dateFormat.parse((String) requestData.get("to"));

        String generatedFile;
        List<Object[]> productStats = productService.getProductsStatsBetweenDates(fromDate, toDate);
         if ("csv".equals(type)) {
            generatedFile = reportGenerator.generateProductStatsCsv(productStats,(String) requestData.get("from"), (String) requestData.get("to"));
        } else {
            generatedFile = reportGenerator.generateProductStatsPdf(productStats, (String) requestData.get("from"), (String) requestData.get("to"));
        }


        File requestedFile = new File(generatedFile);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(requestedFile.toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType("csv".equals(type) ? MediaType.parseMediaType("text/csv") : MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", requestedFile.getName());
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/chart")
    @ResponseBody
    public Map<String, Object> getSalesData() {
        List<Long> salesData = orderService.findAllOrderCountForEachMonth();
        List<Double> revenueData = orderService.getTotalAmountForEachMonth();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("salesData", salesData);
        responseData.put("revenueData", revenueData);
        return responseData;
    }

    private static class DateRange {
        private final Date startDate;
        private final Date endDate;

        public DateRange(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }
    }
}
