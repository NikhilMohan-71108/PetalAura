package com.petalaura.admin.controller;

import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public String customer(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
           return "redirect:/login";
        }
          List<Customer> customers = customerService.findAll();
          model.addAttribute("customers", customers);
          model.addAttribute("size", customers.size());
          return "customers";
    }

    @GetMapping("/disable-customer/{id}")
    public String disableCustomer(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
       redirectAttributes.addFlashAttribute("success","Customer disabled");
         customerService.blockById(id);
         return "redirect:/customers";
    }
    @GetMapping("/enable-customer/{id}")
    public String enableCustomer(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success","Customer enabled");
        customerService.enableById(id);
        return "redirect:/customers";
    }

}
