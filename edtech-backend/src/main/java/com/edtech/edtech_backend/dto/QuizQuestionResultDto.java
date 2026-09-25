package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuizQuestionResultDto {
    private Long questionId;
    private String selectedAnswer;
    private String correctAnswer;
    private Boolean correct;
    private String description;
}
