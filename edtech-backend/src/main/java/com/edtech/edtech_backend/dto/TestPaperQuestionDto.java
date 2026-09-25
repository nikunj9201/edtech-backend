package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestPaperQuestionDto {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String description;
    private Integer marks;
    private Double negativeMarks;
    private Integer displayOrder;
}
