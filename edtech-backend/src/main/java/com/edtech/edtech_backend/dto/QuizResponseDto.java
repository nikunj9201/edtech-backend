package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private Long categoryId;
    private ContentAccessType accessType;
    private int questionCount;
}
