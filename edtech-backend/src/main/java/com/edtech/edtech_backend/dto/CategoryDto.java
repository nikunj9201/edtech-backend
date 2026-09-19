package com.edtech.edtech_backend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryDto {

    private String name;

    private String description;

    // Old multipart upload
    private MultipartFile icon;

    // New R2 direct upload
    private String iconUrl;

    private Long parentId;
}