package com.petalaura.admin.controller;


import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.utils.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ReferalController {
    @Autowired
    CustomerService customerService;
    @PostMapping("/sendReferal")
    public String processReferalCode(HttpServletRequest request, Model model, Principal principal) {
        String email = request.getParameter("email");
        String token = RandomStringUtils.randomAlphanumeric(30);
        String username = principal.getName();
      //  customerService.updateReferalCodeToken(token,username);
        String referalLink= Utility.getSiteURL(request)+"/referal_link?token="+token;
       // sendEmail(email,referalLink);
        model.addAttribute("message","We have sent a referal code for you");
        return "redirect:/account";



    }

//    private void sendEmail(String email, String referalLink) throws MessagingException {
//
//        MimeMessage message = javaMailSender.createMimeMessage();
//
//        // Use MimeMessageHelper to simplify setting up the message content
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//        // Set the recipient email, subject, and the email body content
//        helper.setTo(toEmail);  // This is where the toEmail variable is used
//        helper.setSubject("Your Referral Code");
//        helper.setText("Here is your referral link: " + referalLink);
//
//        try {
//            // Send the email via JavaMailSender
//            javaMailSender.send(message);
//            System.out.println("Email sent successfully to " + toEmail);
//        } catch (MailException e) {
//            // Handle errors in case the email fails to send
//            System.out.println("Error sending email to " + toEmail + ": " + e.getMessage());
//        }
//    }

}
