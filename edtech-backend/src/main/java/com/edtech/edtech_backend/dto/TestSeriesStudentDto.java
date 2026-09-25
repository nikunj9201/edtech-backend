package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestSeriesStudentDto {
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private ContentAccessType accessType;
    private Boolean isPremium;
    private Boolean locked;
}
