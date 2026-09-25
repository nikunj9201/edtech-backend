package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.SubscriptionStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserSubscriptionDto {
    private Long courseId;
    private boolean isSubscribed;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private long daysRemaining;
    private SubscriptionStatus status;
    private String transactionId;
}
