package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestSeriesSubscriptionActivationRequest {
    private Long userId;
    private Long testSeriesId;
    private Long planId;
    private int durationInDays;
}
