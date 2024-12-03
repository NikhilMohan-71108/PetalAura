package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.UserOtpRepository;
import com.petalaura.library.Service.UserOtpService;
import com.petalaura.library.model.UserOtp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOtpServiceImpl implements UserOtpService {
    @Autowired
    private UserOtpRepository userOTPRepository;


    public UserOtpServiceImpl(UserOtpRepository userOTPRepository) {
        this.userOTPRepository = userOTPRepository;
    }

    @Override
    public void saveOrUpdate(UserOtp userOTP) {
        userOTPRepository.save(userOTP);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userOTPRepository.existsByEmail(email);
    }

    @Override
    public UserOtp findByEmail(String email) {
        return userOTPRepository.findByEmail(email);
    }
}
