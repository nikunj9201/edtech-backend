package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private boolean isPremium;
}