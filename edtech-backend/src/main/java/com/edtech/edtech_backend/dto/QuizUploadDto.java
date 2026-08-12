package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuizUploadDto {
    private String title;
    private Long courseId;
    private boolean isFree;
}