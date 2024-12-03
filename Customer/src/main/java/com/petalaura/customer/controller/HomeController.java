package com.petalaura.customer.controller;


import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Category;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping(value = {"/","/index"})
    public String index(Model model) {
      List<Category> categories = categoryService.findAll();
      List<ProductDto> productsDtos = productService.findAll();
      model.addAttribute("categories", categories);
      model.addAttribute("products", productsDtos);
      return "index";
    }

    @GetMapping("/home")
    public String index(Model model, HttpSession session, Principal principal){
        if(principal != null){
            session.setAttribute("email",principal.getName());
        }else{
            session.removeAttribute("email");
        }
        List<Category> categories= categoryService.findAll();
        List<ProductDto> productDtos=productService.findAll();
        model.addAttribute("categories",categories);
        model.addAttribute("products",productDtos);
        return "home";
    }
}
