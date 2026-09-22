package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePurchaseRequestDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private BigDecimal amount;
    private PurchaseRequestStatus status;
    private LocalDateTime createdAt;
}
