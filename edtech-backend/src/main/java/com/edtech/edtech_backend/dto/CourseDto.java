package com.edtech.edtech_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.fasterxml.jackson.annotation.JsonAlias;

@Data
public class CourseDto {
    private Long categoryId;
    private String title;
    private String subtitle;
    private String description;

    @JsonAlias({"thumbnail_url","thumbnailUrl"})
    private String thumbnailUrl;

    @JsonAlias({"price","original_price","originalPrice"})
    private BigDecimal originalPrice;

    @JsonAlias({"discounted_price","discountedPrice"})
    private BigDecimal discountedPrice;

    @JsonAlias({"accessType","access_type"})
    private ContentAccessType accessType = ContentAccessType.FREE;
}
