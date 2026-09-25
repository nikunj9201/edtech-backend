package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class QuizSubmissionDto {
    // Backward-compatible: Map of questionId -> selectedOptionIndex (0-3)
    private Map<Long, Integer> answers;

    // New format: list of answers where selectedAnswer can be "A", "B", "C", "D" or numeric index
    private List<QuestionAnswerRequestDto> answerList;

    public Map<Long, Integer> getNormalizedAnswers() {
        Map<Long, Integer> normalized = new HashMap<>();
        if (answers != null) {
            normalized.putAll(answers);
        }
        if (answerList != null) {
            for (QuestionAnswerRequestDto item : answerList) {
                if (item == null || item.getQuestionId() == null) {
                    continue;
                }
                Integer optionIndex = item.getSelectedOptionIndex();
                if (optionIndex == null && item.getSelectedAnswer() != null) {
                    optionIndex = parseAnswerIndex(item.getSelectedAnswer());
                }
                if (optionIndex != null) {
                    normalized.put(item.getQuestionId(), optionIndex);
                }
            }
        }
        return normalized;
    }

    private Integer parseAnswerIndex(String selectedAnswer) {
        if (selectedAnswer == null) {
            return null;
        }
        String normalized = selectedAnswer.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (upper.equals("A")) return 0;
        if (upper.equals("B")) return 1;
        if (upper.equals("C")) return 2;
        if (upper.equals("D")) return 3;
        try {
            int value = Integer.parseInt(normalized);
            if (value >= 0 && value <= 3) {
                return value;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
