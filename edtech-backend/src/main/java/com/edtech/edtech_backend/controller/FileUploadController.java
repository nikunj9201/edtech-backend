package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.PresignedUrlRequestDto;
import com.edtech.edtech_backend.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URL;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Value("${cloudflare.r2.bucket-name:}")
    private String defaultBucketName;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/generate-presigned-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generatePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        String bucketName = requestDto.getBucketName();
        if (bucketName == null || bucketName.isBlank()) {
            if (defaultBucketName == null || defaultBucketName.isBlank()) {
                // Return clear error so frontend can show a helpful message
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("R2 bucket name is not configured.");
            }
            bucketName = defaultBucketName;
        }

        URL url = fileStorageService.generatePresignedUrl(bucketName, requestDto.getObjectKey(), 10);
        return ResponseEntity.ok(url.toString());
    }

    // Public GET endpoint to generate a download presigned URL from objectKey query param.
    // This is permitted in SecurityConfig as GET /api/files/generate-download-url is explicitly allowed.
    @GetMapping("/generate-download-url")
    public ResponseEntity<String> generateDownloadUrl(@RequestParam("objectKey") String objectKey,
                                                      @RequestParam(value = "bucketName", required = false) String bucketName) {
        if (bucketName == null || bucketName.isBlank()) {
            if (defaultBucketName == null || defaultBucketName.isBlank()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("R2 bucket name is not configured.");
            }
            bucketName = defaultBucketName;
        }

        URL url = fileStorageService.generateDownloadPresignedUrl(bucketName, objectKey, 10);
        return ResponseEntity.ok(url.toString());
    }
}