package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpDto {
    private String name;
    private String email;
    private String password;
    private String contactNumber;
    private String country;
    private String city;
    private LocalDate dateOfBirth;
}