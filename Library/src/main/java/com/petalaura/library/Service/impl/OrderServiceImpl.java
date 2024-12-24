package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.*;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.Service.WalletService;
import com.petalaura.library.dto.DailyEarning;
import com.petalaura.library.dto.Monthlyearning;
import com.petalaura.library.dto.WeeklyEarnings;
import com.petalaura.library.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartRepository shopingCartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Autowired
    private WalletService walletService;



    @Override
    @Transactional
    public Order saveOrder(ShoppingCart shoppingCart, String email, Long addressId, String paymentMethod, Double grandTotal) {
        if (paymentMethod.equalsIgnoreCase("cash_on_delivery") && grandTotal > 1000) {
            throw new IllegalArgumentException("Cash on Delivery is not allowed for orders above Rs 1000.");
        }

        Order order = new Order();
        order.setOrderDate(new Date());
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Pending");  // Initial payment status
        order.setCustomer(customerRepository.findByEmail(email));
        order.setGrandTotalPrize(grandTotal);
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(addressRepository.getReferenceById(addressId));
        orderRepository.save(order);

        List<ShoppingCart> shoppingCarts = shopingCartRepository.findShoppingCartByCustomer(email);
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setProduct(cart.getProduct());
            orderDetails.setOrder(order);
            orderDetails.setQuantity(cart.getQuantity());
            orderDetails.setUnitPrice(cart.getUnitPrice());
            orderDetails.setTotalPrice(cart.getTotalPrice());
            orderDetailsRepository.save(orderDetails);

            Product product = cart.getProduct();
            int quantity = product.getCurrentQuantity() - cart.getQuantity();
            product.setCurrentQuantity(quantity);
            productRepository.save(product);

//            cart.setDeleted(true);
            shopingCartRepository.save(cart);
        }

        return order;
    }

    public void deleteCart(String email){
        List<ShoppingCart> shoppingCarts = shopingCartRepository.findShoppingCartByCustomer(email);
        ShoppingCart cart = shoppingCarts.stream().findFirst().get();
        cart.setDeleted(true);
        shopingCartRepository.save(cart);

    }

    @Override
    public boolean isCodAllowed(Double grandTotal) {
        return grandTotal <= 1000;
    }

    @Override
    public List<OrderDetails> findAllOrder() {
        return orderDetailsRepository.findAllOrder();
    }

    @Override
    public List<OrderDetails> findOrderDetailsByCustomer(String email) {
        return orderDetailsRepository.findOrderDetailsByCustomer(email);
    }

    @Override
    public List<OrderDetails> findByOrderId(Long orderId) {
        return orderDetailsRepository.findByOrderId(orderId);
    }

    @Override
    public Order findById(long id) {
        return orderRepository.getReferenceById(id);
    }

    @Override
    public void updateOrderStatus(Order order) {

        Order order1 = orderRepository.getReferenceById(order.getId());

        order1.setOrderStatus(order.getOrderStatus());
        orderRepository.save(order1);
        if(order.getOrderStatus().equals("Return Accept")){
            walletService.addToRefundAmount(order.getId());
//            Customer customer = order.getCustomer();
//            Wallet wallet=customer.getWallet();
            //wallet.setBalance(wallet.getBalance()+order.getGrandTotalPrize());
            List<OrderDetails> orderDetails=orderDetailsRepository.findByOrderId(order.getId());
            for(OrderDetails orders:orderDetails){
                Long productId=orders.getProduct().getId();
                Product product=productRepository.getReferenceById(productId);
                product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
                productRepository.save(product);
            }

        }

        else if(order.getOrderStatus().equals("Delivered")){
            Order order2 = orderRepository.getReferenceById(order.getId());
            order2.setDeliveryDate(new Date());
            orderRepository.save(order2);
        }

    }



    @Override
    public List<Order> findAll() {
        return orderRepository.findAllByDate();
    }

    @Override
    public void cancelOrder(Long id) {

        Order order = orderRepository.getReferenceById(id);
        order.setOrderStatus("Cancel");
        order.setPaymentStatus("Order Cancelled");
        orderRepository.save(order);
        List<OrderDetails> orderDetails=orderDetailsRepository.findByOrderId(id);
        for(OrderDetails orders:orderDetails){
            Long productId=orders.getProduct().getId();
            Product product=productRepository.getReferenceById(productId);
            product.setCurrentQuantity(product.getCurrentQuantity()+orders.getQuantity());
            productRepository.save(product);
        }

    }


    @Override
    public Order findOrderByIdAndCustomerEmail(Long orderId, String email) {
        return orderRepository.findOrderByIdAndCustomerEmail(orderId, email);
    }

    @Override
    public void returnOrder(Long id) {
        Order order = orderRepository.getReferenceById(id);
        order.setOrderStatus("Return Pending");
        orderRepository.save(order);
    }

    @Override
    public Double getTotalOrderAmount() {

        return orderRepository.getTotalConfirmedOrdersAmount();
    }


    @Override
    public List<Order> findOrderByCustomer(String email) {
        // Pageable pageable=PageRequest.of(pageNo,6);
        // Page<Order> orders=this.orderRepository.findOrderByCustomer(email,pageable);
        return orderRepository.findOrderByCustomer(email);
    }

    @Override
    public Page<Order> findOrderByCustomerPagable(int pageNo, String email) {
        Pageable pageable=PageRequest.of(pageNo,6);
        Page<Order> orders=this.orderRepository.findOrderByCustomerPagable(pageable,email);
        return orders;
    }

    @Override
    public Page<Order> findOrderByOrderStatusPagable(int pageNo, String status) {
        Pageable pageable=PageRequest.of(pageNo,6);
        Page<Order> orders=this.orderRepository.findOrderByOrderStatusPagable(pageable,status);
        return orders;
    }

    @Override

    public List<Order> getDailyOrders(LocalDate date) {
        LocalDate startOfDay = date.atStartOfDay().toLocalDate();
        LocalDate endOfDay = startOfDay.plusDays(1);
        return orderRepository.findByOrderDateBetween(startOfDay, endOfDay);
    }

    @Override
    public Page<Order> findOrderByPageble(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = this.orderRepository.findOrderByPagable(pageable);
        return orders;
    }

    @Override
    public List<Order> getDailyReport(Date date) {
        // Call the repository method to get daily orders
        return orderRepository.findDailyOrders(date);
    }



    @Override
    public void deleteOrderDetailsById(Long id) {
        orderDetailsRepository.deleteOrderDetailsById(id);
    }

    @Override
    public List<Order> findOrdersByAddressId(Long addressId) {
        return orderRepository.findByShippingAddressId(addressId);
    }

    @Override
    public List<Long> findOrderIdsByAddressId(Long addressId) {
        List<Order> orders = orderRepository.findByShippingAddressId(addressId);
        return orders.stream().map(Order::getId).collect(Collectors.toList());
    }

    @Override
    public List<Monthlyearning> getMonthlyReport(int year) {
        List<Object[]> result = orderRepository.monthlyReport(year);
        List<Monthlyearning> report = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM"); // Year-Month format

        for (Object[] row : result) {
            String monthString = (String) row[0];  // Assuming the month is returned as a String like "2024-12"

            try {
                Date month = sdf.parse(monthString);  // Parsing the string to a Date object
                Double grandTotal = (Double) row[1];
                Long totalOrder = (Long) row[2];
                Long deliveredOrders = (Long) row[3];
                Long cancelledOrders = (Long) row[4];

                report.add(new Monthlyearning(month, grandTotal, totalOrder, deliveredOrders, cancelledOrders));
            } catch (ParseException e) {
                e.printStackTrace();  // Handle the parsing exception
            }
        }
        return report;
    }


    @Override
    public List<DailyEarning> dailyReport(int year, int month) {
        List<Object[]> result=orderRepository.dailyReport(year,month);
        List<DailyEarning> report=new ArrayList<>();
        for(Object[] row:result){
            Date date= (Date) row[0];
            Double grandTotel= (Double) row[1];
            Long totelOrder= (Long) row[2];
            report.add(new DailyEarning(date,grandTotel,totelOrder));
        }

        return report;
    }


//    @Override
//    public List<WeeklyEarnings> findWeeklyEarnings(int year) {
//        List<Object[]> result=orderRepository.weeklyEarnings(year);
//        List<WeeklyEarnings> report=new ArrayList<>();
//        for (Object[] row:result){
//            Date date= (Date) row[0];
//            Double earnings=(Double)  row[1];
//            report.add(new WeeklyEarnings(date,earnings));
//
//        }
//        return report;
//    }
@Override
public List<WeeklyEarnings> findWeeklyEarnings(int year) {
    List<Object[]> result = orderRepository.weeklyEarnings(year);
    List<WeeklyEarnings> report = new ArrayList<>();

    // Change the format to handle "yyyy-ww"
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-ww");

    for (Object[] row : result) {
        Date date = null;

        if (row[0] instanceof String) {
            try {
                // Try to parse the string as "yyyy-ww" first
                String weekString = (String) row[0];
                // Check if the string follows the year-week format
                if (weekString.length() == 7) {  // e.g., "2024-50"
                    date = getDateFromYearAndWeek(weekString);
                } else {
                    // Otherwise, fall back to parsing as a regular date (yyyy-MM-dd)
                    date = sdf.parse(weekString);  // For regular date formats
                }
            } catch (ParseException e) {
                e.printStackTrace();  // Log the exception appropriately
            }
        } else if (row[0] instanceof Date) {
            date = (Date) row[0];  // Directly assign if already a Date
        }

        Double earnings = (Double) row[1];
        report.add(new WeeklyEarnings(date, earnings));
    }
    return report;
}

    private Date getDateFromYearAndWeek(String yearWeek) {
        try {
            String[] parts = yearWeek.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);

            // Get the first day of the specified week in the year
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.WEEK_OF_YEAR, week);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  // You can choose which day of the week to represent

            return calendar.getTime();
        } catch (NumberFormatException e) {
            e.printStackTrace();  // Handle invalid input format
            return null;
        }
    }


    @Override
    public Double getTotalAmountForMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate=currentDate.withDayOfMonth(1);
        LocalDate endDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        Double totalAmount = orderRepository.getTotalConfirmedOrdersAmountForMonth(startDate,endDate,"Delivered");

        return totalAmount;
    }

    @Override
    public List<Long> findAllOrderCountForEachMonth() {
        List<Long>orderCounts=new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);

        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());

            long orderCount= orderRepository.countByOrderDateBetweenAndOrderStatus(localStartDate,localEndDate,"Delivered");
            orderCounts.add(orderCount);
            currentDate = currentDate.plusMonths(1);

        }


        return orderCounts;
    }

    @Override
    public List<Double> getTotalAmountForEachMonth() {

        List<Double> totalRevenuePerMonth = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().withMonth(1);

        for(int i=0 ; i < 12 ; i++){
            LocalDate localStartDate=currentDate.withDayOfMonth(1);
            LocalDate localEndDate=currentDate.withDayOfMonth(currentDate.lengthOfMonth());

            Double totalRevenue = orderRepository.getTotalConfirmedOrdersAmountForMonth(localStartDate,localEndDate,"Delivered");
            totalRevenuePerMonth.add(totalRevenue);
            currentDate = currentDate.plusMonths(1);

        }

        return totalRevenuePerMonth;
    }

}
