package com.petalaura.library.Service.impl;

import com.petalaura.library.Service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private JavaMailSender javaMailSender;

//    @Value("${spring.mail.username}")
  //  private String sender;
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    private String generateEmailOtpVarificationMessage(String otp) {
        String message ="Hello Customer "
                +"One Time Password for verification is: "+otp
                +" Note: this OTP is set to expire in 5 minutes.";
        return message;
    }


    @Override
    public String sendSimpleEmail(String email, String otp) {
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();
            String message = generateEmailOtpVarificationMessage(otp);
            // Setting up necessary details
            //mailMessage.setFrom(sender);
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText(message);
            mailMessage.setSubject("Email Verification for  PETALAURA");

            // Sending the mail
            javaMailSender.send(mailMessage);

            return "success";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }


}


