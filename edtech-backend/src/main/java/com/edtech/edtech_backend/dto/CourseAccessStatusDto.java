package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseAccessStatusDto {
    private Long courseId;
    private boolean accessGranted;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private long daysRemaining;
}
