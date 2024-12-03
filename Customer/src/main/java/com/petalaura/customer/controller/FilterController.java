package com.petalaura.customer.controller;


import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.CategoryDto;
import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class    FilterController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/high-price")
    public String highPrice(Model model, @RequestParam(defaultValue = "0") int pageNo, HttpServletRequest request) {
        List<CategoryDto> categories = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categories);
        int pageSize = 10;
        Page<ProductDto> products = productService.filterHighProducts(pageNo, pageSize);
        List<ProductDto> listView = productService.listViewProducts();
        HttpSession session = request.getSession();
        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
        model.addAttribute("title", "Shop Detail");
        model.addAttribute("page", "Shop Detail");
        model.addAttribute("productViews", listView);
        model.addAttribute("products", products);

        // Pass pagination-related attributes to the view
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "shop-list";


    }

    @GetMapping("/lower-price")
    public String lowPrice(Model model, @RequestParam(defaultValue = "0") int pageNo, HttpServletRequest request) {
        List<CategoryDto> categories = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categories);
        int pageSize = 10;
        Page<ProductDto> products = productService.filterLowProducts(pageNo, pageSize);
        List<ProductDto> listView = productService.listViewProducts();
        HttpSession session = request.getSession();
        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
        model.addAttribute("title", "Shop Detail");
        model.addAttribute("page", "Shop Detail");
        model.addAttribute("productViews", listView);
        model.addAttribute("products", products);

        // Pass pagination-related attributes to the view
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "shop-list";


    }

    @GetMapping("/popular")
    public String popular(Model model, @RequestParam(defaultValue = "0") int pageNo, HttpServletRequest request) {
        List<CategoryDto> categories = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categories);
        List<Product> products = productService.filterByPopularity();
        List<ProductDto> listView = productService.listViewProducts();
        HttpSession session = request.getSession();
        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
        model.addAttribute("title", "Shop Detail");
        model.addAttribute("page", "Shop Detail");
        model.addAttribute("productViews", listView);
        model.addAttribute("products", products);

        // Pass pagination-related attributes to the view
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", 1);

        return "shop-list";


    }

    @GetMapping("/ascending-name")
    public String filterByNameAscending(Model model, @RequestParam(defaultValue = "0") int pageNo,
                                        HttpServletRequest request) {

        List<CategoryDto> categories = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categories);
        int pageSize = 10;
        Page<ProductDto> products = productService.filterByNameAscending(pageNo, pageSize);
        List<ProductDto> listView = productService.listViewProducts();
        HttpSession session = request.getSession();
        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
        model.addAttribute("title", "Shop Detail");
        model.addAttribute("page", "Shop Detail");
        model.addAttribute("productViews", listView);
        model.addAttribute("products", products);

        // Pass pagination-related attributes to the view
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "shop-list";
    }


    @GetMapping("/descending-name")
    public String filterByNameDescending(Model model, @RequestParam(defaultValue = "0") int pageNo,
                                         HttpServletRequest request) {

        List<CategoryDto> categories = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categories);
        int pageSize = 10;
        Page<ProductDto> products = productService.filterByNameDescending(pageNo, pageSize);
        List<ProductDto> listView = productService.listViewProducts();
        HttpSession session = request.getSession();
        session.setAttribute("currentPageUrl", request.getRequestURL().toString());
        model.addAttribute("title", "Shop Detail");
        model.addAttribute("page", "Shop Detail");
        model.addAttribute("productViews", listView);
        model.addAttribute("products", products);

        // Pass pagination-related attributes to the view
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());

        return "shop-list";
    }
}

