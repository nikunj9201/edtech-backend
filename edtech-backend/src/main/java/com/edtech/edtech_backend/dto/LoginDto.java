package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}