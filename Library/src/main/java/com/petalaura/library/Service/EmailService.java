package com.petalaura.library.Service;

import org.springframework.stereotype.Repository;

@Repository
public interface EmailService {
public String sendSimpleEmail(String email,String otp);
}
