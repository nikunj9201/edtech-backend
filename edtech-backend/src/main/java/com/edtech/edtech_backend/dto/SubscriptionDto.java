package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private LocalDate subscriptionDate;
    private LocalDate expiryDate;
}