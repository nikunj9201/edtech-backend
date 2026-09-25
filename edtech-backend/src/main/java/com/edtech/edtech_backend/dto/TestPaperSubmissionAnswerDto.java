package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestPaperSubmissionAnswerDto {
    private Long questionId;
    private String selectedAnswer;
}
