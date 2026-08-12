package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class VideoUploadDto {
    private String title;
    private Long courseId;
    private String videoUrl;
    private boolean isFreeSample;
}