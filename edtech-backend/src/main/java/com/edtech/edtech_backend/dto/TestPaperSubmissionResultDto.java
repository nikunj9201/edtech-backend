package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestPaperSubmissionResultDto {
    private Long paperId;
    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unattemptedQuestions;
    private Integer totalMarks;
    private Integer obtainedMarks;
    private Double percentage;
    private List<TestPaperQuestionResultDto> questions = new ArrayList<>();
}
