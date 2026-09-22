package com.edtech.edtech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String country;
    private String city;
    private LocalDate dateOfBirth;
    private String role;
}
