package com.petalaura.customer.controller;


import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.WishListService;
import com.petalaura.library.dto.CustomerDto;
import com.petalaura.library.model.Customer;
import com.petalaura.library.model.UserOtp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    CustomerService customerService;
    @Autowired
    WishListService wishListService;
    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {

            return "redirect:/home";
        }
        Object attribute = session.getAttribute("userLoginID");
        if (attribute != null) {

            return "redirect:/home";
        }
        CustomerDto customerDto = new CustomerDto();
        if (customerDto.isBlocked()) {
            return "redirect:/login?blocked";
        }
        return "login";
    }


    @GetMapping("/register")
     public String register(Model model) {
        String email = (String) model.asMap().get("email");
        CustomerDto customerDto=new CustomerDto();
        customerDto.setEmail(email);
        model.addAttribute("customerDto",customerDto);
        return "register";
     }

     @PostMapping("/do-register")
     public String processRegister(@Valid @ModelAttribute("customerDto")
                                   CustomerDto customerDto, BindingResult result, Model model,  HttpSession session){
        try{
        if(result.hasErrors()) {
            return "register";
        }

           Customer existingCustomer = customerService.findByEmail(customerDto.getEmail());
              if(existingCustomer!=null) {
                 model.addAttribute("customer",customerDto);
                 session.setAttribute("error","Email already in use");
                 return "register";
              }

        if(customerDto.getPassword().equals(customerDto.getRepeatPassword())) {
            customerService.save(customerDto);
            model.addAttribute("success","Registered Successfully ");
           return  "redirect:/login";
        }else{
            session.setAttribute("error", "Passwords do not match");
            return "register";
        }
     } catch (Exception e) {
             e.printStackTrace();
              session.setAttribute("error","server error,try again");
        }
        return "register";
     }

     @GetMapping("/verify-email")
        public String getForgotPassword(){
                return "verify-email";
     }

     @GetMapping("/verifyEmail")
          public String showVerifyEmail(){
           return "verify-email";
     }

     @GetMapping("/otpvalidation")
     public String showotpValidationPage(Model model, HttpSession session) {
         String email = (String) model.getAttribute("email");
         UserOtp userOTP = new UserOtp();
         userOTP.setEmail(email);
         session.setAttribute("email",email);
         model.addAttribute("userOTP",userOTP);
          return "verify-otp";
    }



}

