package com.edtech.edtech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentItemDTO {
    private Long id;
    private String title;
    private String type;
    private boolean isPremium;
    private Long categoryId;
    private String fileUrl;
}