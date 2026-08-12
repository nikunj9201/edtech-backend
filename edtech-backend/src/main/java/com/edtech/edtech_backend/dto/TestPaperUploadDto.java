package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class TestPaperUploadDto {
    private String title;
    private Long courseId;
    private String paperUrl;
    private boolean isFree;
}