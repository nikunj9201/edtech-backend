package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class QuizSubmissionResponseDto {
    private int totalQuestions;
    private int correctAnswers;
    private double percentage;
    private int marksObtained;
}
