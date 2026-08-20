package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuizQuestionDto {
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer correctOptionIndex; // 0-3
}
