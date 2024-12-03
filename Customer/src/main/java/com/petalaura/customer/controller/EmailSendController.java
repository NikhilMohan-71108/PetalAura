package com.petalaura.customer.controller;

import com.petalaura.library.Service.CustomerService;
import com.petalaura.library.Service.EmailService;
import com.petalaura.library.Service.OtpService;
import com.petalaura.library.Service.UserOtpService;
import com.petalaura.library.model.UserOtp;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Date;

@Controller

public class EmailSendController {


        private OtpService otpService;
        private EmailService emailService;
        private UserOtpService userOTPService;

        private CustomerService usersSevice;
        private PasswordEncoder passwordEncoder;

        public EmailSendController(OtpService otpService,
                                   EmailService emailService,
                                   UserOtpService userOTPService,
                                   CustomerService usersSevice,
                                   PasswordEncoder passwordEncoder
        ) {
            this.otpService = otpService;
            this.emailService = emailService;
            this.userOTPService = userOTPService;
            this.usersSevice = usersSevice;
            this.passwordEncoder = passwordEncoder;
        }

        @PostMapping("/sendVerificationEmailOtp")
        public String sendVerificationEmailOtp(
                @RequestParam("email") String email
                , HttpSession session,
                RedirectAttributes redirectAttributes) throws Exception {

            if (usersSevice.findByEmail(email) == null) {
                String otp = otpService.generateOtp();
                if (!userOTPService.existsByEmail(email)) {
                    // new email verification
                    UserOtp userOTP = new UserOtp();
                    userOTP.setEmail(email);
                    userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                    userOTP.setCreatedAt(new Date());
                    userOTP.setOtpRequestedTime(new Date());
                    userOTP.setUpdateOn(new Date());
                    try {
                        userOTPService.saveOrUpdate(userOTP);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Couldn't finish OTP verification process" + HttpStatus.BAD_REQUEST);
                    }

                } else {
                    //code to delete all data related to this email id
                    UserOtp userOTP = userOTPService.findByEmail(email);
                    userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                    userOTP.setOtpRequestedTime(new Date());
                    userOTP.setUpdateOn(new Date());
                    try {
                        userOTPService.saveOrUpdate(userOTP);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Couldn't finish OTP verification process");
                    }
                }
                String status = emailService.sendSimpleEmail(email, otp);
                if (status.equals("success")) {
                    session.setAttribute("message", "otpsent");
                    redirectAttributes.addFlashAttribute("email", email);
                    return "redirect:/otpvalidation";

                } else {
                    return "redirect:/verifyEmail?error";
                }
            } else {
                return "redirect:/verifyEmail?existUser";
            }

        }

        @PostMapping("/sendEmailOTPLogin")
        public String sendEmailOTPLogin(
                @RequestParam("email") String email
                , HttpSession session,
                RedirectAttributes redirectAttributes) throws Exception {
            if (usersSevice.findByEmail(email) != null) {
                String otp = otpService.generateOtp();
                UserOtp userOTP = userOTPService.findByEmail(email);
                if (userOTP != null) {
                    userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                    userOTP.setOtpRequestedTime(new Date());
                    userOTP.setUpdateOn(new Date());
                } else {
                    userOTP = new UserOtp();
                    userOTP.setEmail(email);
                    userOTP.setOneTimePassword(passwordEncoder.encode(otp));
                    userOTP.setCreatedAt(new Date());
                    userOTP.setOtpRequestedTime(new Date());
                    userOTP.setUpdateOn(new Date());
                }
                try {
                    userOTPService.saveOrUpdate(userOTP);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Send OTP.Please try after some time...");
                }
                String status = emailService.sendSimpleEmail(email, otp);
                if (status.equals("success")) {
                    session.setAttribute("message", "otpsent");
                    redirectAttributes.addFlashAttribute("email", email);
                    return "redirect:/forgotPasswordOTPLogin";

                } else {
                    return "redirect:/forgotpassword?error";
                }
            } else {
                return "redirect:/forgotpassword?error";
            }
        }

        @PostMapping("/validateOTP")
        public String validateOTP(@ModelAttribute("userOTP") UserOtp userOTPRequest, HttpSession session,
                                  RedirectAttributes redirectAttributes) {

            String email = (String) session.getAttribute("email");


            if (email == null) {

                return "redirect:/error";
            }


            UserOtp userOTP = userOTPService.findByEmail(email);


            if (userOTP != null) {
                if (passwordEncoder.matches(userOTPRequest.getOneTimePassword(), userOTP.getOneTimePassword())) {

                    redirectAttributes.addFlashAttribute("email", userOTP.getEmail());
                    return "redirect:/register";
                } else {

                    return "redirect:/otpvalidation?error";
                }
            } else {

                return "redirect:/requestOTP?email=" + email; // Redirect to a page to request OTP again
            }

        }
//    @PostMapping("/resendOTP")
//    public String showotpValidationPage(Model model, HttpSession session,Principal principal) {
//        String email = principal.getName();
//      //  String email = (String) model.getAttribute("email");
//        UserOtp userOTP = new UserOtp();
//        userOTP.setEmail(email);
//        session.setAttribute("email",email);
//        model.addAttribute("userOTP",userOTP);
//        return "verify-otp";
//    }



    @PostMapping("/sendNotifyEmail")
        public String sendNotify(
                @RequestParam("product") String product
                , HttpSession session,
                RedirectAttributes redirectAttributes, Principal principal) throws Exception {
            String email = principal.getName();
            String status = emailService.sendSimpleEmail(email, product);
            if (status.equals("success")) {
                session.setAttribute("message", "otpsent");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/find-products";

            } else {
                return "redirect:/find-products?error";

            }
        }
    }

