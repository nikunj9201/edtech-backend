package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmissionDto {
    // Map of questionId -> selectedOptionIndex (0-3)
    private Map<Long, Integer> answers;
}
