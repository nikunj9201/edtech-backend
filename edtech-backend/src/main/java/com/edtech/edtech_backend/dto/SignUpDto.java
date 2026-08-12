package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class SignUpDto {
    private String email;
    private String password;
    private String contactNumber;
    private String country;
}