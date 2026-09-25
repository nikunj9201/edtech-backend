package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuizQuestionStudentDto {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}
