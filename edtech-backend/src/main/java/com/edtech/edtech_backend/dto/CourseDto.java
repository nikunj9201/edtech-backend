package com.edtech.edtech_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseDto {
    private Long categoryId;
    private String title;
    private String subtitle;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
}
