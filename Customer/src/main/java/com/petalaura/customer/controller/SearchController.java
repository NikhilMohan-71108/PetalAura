package com.petalaura.customer.controller;


import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.model.Category;
import com.petalaura.library.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;


    @GetMapping("/searchCategoryHome")
    public String showCategoryFilterHome(@RequestParam("name") String categoryName, Model model,
                                         HttpSession session, HttpServletRequest request) {
       List<Product> products= productService.findAllByCategoryName(categoryName);
       int size=products.size();
       model.addAttribute("products", products);
       model.addAttribute("size", size);
       List<Category> category= categoryService.findAllByActivatedTrue();
       model.addAttribute("categories", category);
       return "search_result";
    }

    @GetMapping("/filterSearch")
    public  String showFilterSearch(@RequestParam("key") String key,
                                    Model model, HttpSession session, HttpServletRequest request) {
        List<Product> products = productService.findByNameStartingWithIgnoreCase(key);
        model.addAttribute("products", products);
        int size=products.size();
        model.addAttribute("size", size);
        List<Category> category= categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", category);
        return "search_result";
    }
}
