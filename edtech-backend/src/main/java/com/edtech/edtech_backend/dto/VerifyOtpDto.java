package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class VerifyOtpDto {
    private String email;
    private String otp;
    private String password;
    private String contactNumber;
    private String country;
}