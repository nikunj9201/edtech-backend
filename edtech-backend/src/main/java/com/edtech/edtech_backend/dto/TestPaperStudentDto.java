package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestPaperStudentDto {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer totalQuestions;
    private Boolean locked;
}
