package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class SubscriptionActivationRequest {
    private Long userId;
    private Long courseId;
    private Long planId;
    private int durationInDays;
}
