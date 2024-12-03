package com.petalaura.admin.controller;


import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SearchController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;




    
}
