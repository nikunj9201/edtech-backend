package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.PresignedUrlRequestDto;
import com.edtech.edtech_backend.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/generate-presigned-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generatePresignedUrl(@RequestBody PresignedUrlRequestDto requestDto) {
        URL url = fileStorageService.generatePresignedUrl(requestDto.getBucketName(), requestDto.getObjectKey(), 10);
        return ResponseEntity.ok(url.toString());
    }
}