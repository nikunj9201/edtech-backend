package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private String name;
    private String description; // Added description
    private String iconUrl;
    private Long parentId;
}
