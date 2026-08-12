package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class PresignedUrlRequestDto {
    private String bucketName;
    private String objectKey;
}