package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;

@Data
public class TestPaperUploadDto {
    private String title;
    private Long courseId;
    private String paperUrl;
    private ContentAccessType accessType;
}
