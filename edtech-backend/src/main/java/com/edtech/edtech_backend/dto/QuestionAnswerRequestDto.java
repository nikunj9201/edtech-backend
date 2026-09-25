package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuestionAnswerRequestDto {
    private Long questionId;
    private String selectedAnswer;
    private Integer selectedOptionIndex;
}
