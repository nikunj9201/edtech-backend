package com.edtech.edtech_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestPaperSubmissionRequestDto {
    private List<TestPaperSubmissionAnswerDto> answers;
}
