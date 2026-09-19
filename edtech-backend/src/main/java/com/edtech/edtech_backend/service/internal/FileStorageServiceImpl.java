package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.service.FileStorageService;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final S3Presigner s3Presigner;

    public FileStorageServiceImpl(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    // =========================================================
    // PUT PRESIGNED URL
    // Used for uploading files to R2
    // =========================================================
    @Override
    public URL generatePresignedUrl(
            String bucketName,
            String objectKey,
            long expirationMinutes) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(
                                Duration.ofMinutes(expirationMinutes)
                        )
                        .putObjectRequest(objectRequest)
                        .build();

        return s3Presigner
                .presignPutObject(presignRequest)
                .url();
    }


    // =========================================================
    // GET PRESIGNED URL
    // Used for viewing/downloading files from R2
    // =========================================================
    @Override
    public URL generateDownloadPresignedUrl(
            String bucketName,
            String objectKey,
            long expirationMinutes) {

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(
                                Duration.ofMinutes(expirationMinutes)
                        )
                        .getObjectRequest(objectRequest)
                        .build();

        return s3Presigner
                .presignGetObject(presignRequest)
                .url();
    }
}