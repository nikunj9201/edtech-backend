package com.edtech.edtech_backend.service;

import java.net.URL;

public interface FileStorageService {
    URL generatePresignedUrl(String bucketName, String objectKey, long expirationMinutes);
}