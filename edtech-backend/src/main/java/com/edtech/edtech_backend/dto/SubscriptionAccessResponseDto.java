package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAccessResponseDto {
    private Long subscriptionId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long courseId;
    private String courseTitle;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private Long activatedByAdminId;
    private String activatedByAdminName;
}
