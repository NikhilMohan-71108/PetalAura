package com.petalaura.admin.controller;

import com.petalaura.library.Service.AdminService;
import com.petalaura.library.dto.AdminDto;
import com.petalaura.library.model.Admin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class  AuthController {

    @Autowired
    AdminService adminService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request, HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {

            return "redirect:/index";
        }

        Object attribute = session.getAttribute("userLoginID");
        if (attribute != null) {
            
            return "redirect:/index";
        }

        return "login";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("title", "Home Page");

        // Check if the user is authenticated using Spring Security's context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // Redirect to login page if not authenticated
        }

        // Proceed to index page if authenticated
        return "index";
    }


    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        model.addAttribute("title", "Register");
        session.removeAttribute("message");
        model.addAttribute("adminDto", new AdminDto());
        return "register";
    }

    @GetMapping( "/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model,
                              HttpSession session) {

        try {
            session.removeAttribute("message");
            if (result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
           Admin admin = adminService.findByUsername(username);
            if (admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("admin not null");
                session.setAttribute("message","Your email has been registered!");
                //model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                System.out.println("success");
                session.setAttribute("message", "Register successfully!");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                session.setAttribute("message", "Your password is not same!");
                System.out.println("password not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", "Server error! Please try again later");
        }
        return "redirect:/login";

    }


}
