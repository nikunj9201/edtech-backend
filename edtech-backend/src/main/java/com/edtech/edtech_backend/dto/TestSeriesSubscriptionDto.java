package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TestSeriesSubscriptionDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long testSeriesId;
    private String testSeriesTitle;
    private Long planId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private Long activatedByAdminId;
    private String activatedByAdminName;
}
