package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.entity.User;

public interface AuthService {
    String login(LoginDto loginDto);
    User register(SignUpDto signUpDto);
    void generateAndSendOtp(String email);
    boolean verifyOtp(VerifyOtpDto verifyOtpDto);
}