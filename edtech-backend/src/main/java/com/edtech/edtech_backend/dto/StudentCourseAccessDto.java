package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StudentCourseAccessDto {
    private Long courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private ContentAccessType accessType;
    private boolean accessGranted;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private long daysRemaining;
}
