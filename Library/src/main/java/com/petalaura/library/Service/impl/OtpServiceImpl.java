package com.petalaura.library.Service.impl;

import com.petalaura.library.Service.OtpService;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class OtpServiceImpl implements OtpService {
    @Override
    public String generateOtp() {
        Random rand = new Random();
       int number = rand.nextInt(999999);
       return String.format("%06d", number);
    }
}
