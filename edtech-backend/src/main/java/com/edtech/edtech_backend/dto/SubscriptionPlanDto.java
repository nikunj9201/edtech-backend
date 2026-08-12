package com.edtech.edtech_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubscriptionPlanDto {
    private String name;
    private String description;
    private Integer durationInDays;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private boolean active;
}
