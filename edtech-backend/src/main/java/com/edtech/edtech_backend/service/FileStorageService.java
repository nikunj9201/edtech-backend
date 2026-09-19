package com.edtech.edtech_backend.service;

import java.net.URL;

public interface FileStorageService {

    // Generate PUT presigned URL for uploading files
    URL generatePresignedUrl(
            String bucketName,
            String objectKey,
            long expirationMinutes
    );

    // Generate GET presigned URL for viewing/downloading files
    URL generateDownloadPresignedUrl(
            String bucketName,
            String objectKey,
            long expirationMinutes
    );
}