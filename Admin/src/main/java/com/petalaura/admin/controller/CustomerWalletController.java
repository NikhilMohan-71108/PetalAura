package com.petalaura.admin.controller;


import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomerWalletController {
    @Autowired
    CustomerService customerService;
    @GetMapping("/walletCustomer")
    public String showOfferPage(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("size", customers.size());
        return "wallet-customer";


    }

//    @GetMapping("/customers")
//    public String getCustomersPage(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
//                                   @RequestParam(name = "size", defaultValue = "10") int size) {
//        Page<Customer> customersPage = customerService.getCustomersPage(PageRequest.of(page, size));
//        model.addAttribute("customers", customersPage.getContent());
//        model.addAttribute("totalPages", customersPage.getTotalPages());
//        model.addAttribute("currentPage", customersPage.getNumber());
//        model.addAttribute("totalItems", customersPage.getTotalElements());
//        model.addAttribute("size", size);
//        return "wallet-customer"; // Replace with your Thymeleaf template name
//    }
}
