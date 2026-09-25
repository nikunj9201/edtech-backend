package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestPaperRequestDto {
    private String title;
    private String description;
    private String instructions;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer displayOrder;
    private Boolean active;
}
