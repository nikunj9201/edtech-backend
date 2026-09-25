package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizSubmissionResponseDto {
    private Long quizId;
    private int totalQuestions;
    private int attemptedQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int unattemptedQuestions;
    private int totalMarks;
    private int marksObtained;
    private int obtainedMarks;
    private double percentage;
    private List<QuizQuestionResultDto> questions = new ArrayList<>();
}
