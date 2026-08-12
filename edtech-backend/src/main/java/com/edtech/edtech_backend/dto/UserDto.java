package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String contactNumber;
    private String country;
    private String role;
}