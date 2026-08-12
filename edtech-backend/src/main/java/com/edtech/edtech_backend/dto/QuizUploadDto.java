package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.Data;

@Data
public class QuizUploadDto {
    private String title;
    private Long courseId;
    private ContentAccessType accessType;
}
