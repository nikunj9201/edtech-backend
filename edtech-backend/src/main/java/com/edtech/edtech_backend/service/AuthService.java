package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.JwtAuthResponse;
import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.OtpVerificationRequestDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.entity.User;

public interface AuthService {
    JwtAuthResponse login(LoginDto loginDto);
    void requestRegistration(SignUpDto signUpDto);
    User completeRegistration(OtpVerificationRequestDto verificationDto);

    // Forgot-password flow
    void requestPasswordReset(String email);
    void resetPassword(VerifyOtpDto verifyOtpDto);
}