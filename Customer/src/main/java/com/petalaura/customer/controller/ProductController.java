package com.petalaura.customer.controller;

import com.petalaura.library.Service.CategoryService;
import com.petalaura.library.Service.ProductService;
import com.petalaura.library.dto.ProductDto;
import com.petalaura.library.model.Category;
import com.petalaura.library.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    ProductService  productService;

    @Autowired
    CategoryService categoryService;


    @GetMapping("/product-shop/{pageNo}")
         public String shop(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        if (principal == null) {
                 return "redirect:/login";
        }
        Page<Product> products = productService.pageProducts(pageNo);
        List<Category> categories= categoryService.findAllByActivatedTrue();
        model.addAttribute("title", "Shop");
        model.addAttribute("caregories",categories);
        model.addAttribute("size", products.getSize());
        model.addAttribute("products",products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop";
    }
    @GetMapping("/find-products/{id}")
     public String findProduct(@PathVariable("id") Long id, Model model) {
        List<Category> categories=categoryService.findAllByActivatedTrue();
        ProductDto productDto =productService.getById(id);
        List<Product> productDtoList=  productService.findAllByCategory(productDto.getCategory().getId());
        int currentQuantity=productDto.getCurrentQuantity();
        boolean inStock=currentQuantity>=11;
        boolean limitedStock=currentQuantity>= 1 && currentQuantity<=10;
        boolean outOfStock= currentQuantity<=0;
        model.addAttribute("inStock", inStock);
        model.addAttribute("limitedStock", limitedStock);
        model.addAttribute("outOfStock", outOfStock);
        model.addAttribute("categories",categories);
        model.addAttribute("productDto",productDto);
        model.addAttribute("products",productDtoList);
        model.addAttribute("currentQuantity", currentQuantity);
        return "product-description";
    }
}
