package com.edtech.edtech_backend.dto;

import com.edtech.edtech_backend.model.ContentAccessType;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class QuizUploadDto {
    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;

    @NotNull(message = "courseId is required")
    @Positive(message = "courseId must be a positive value")
    private Long courseId;

    @NotNull(message = "categoryId is required")
    @Positive(message = "categoryId must be a positive value")
    @JsonAlias({"category_id", "categoryId"})
    private Long categoryId;

    @NotNull(message = "accessType is required")
    private ContentAccessType accessType;
}
