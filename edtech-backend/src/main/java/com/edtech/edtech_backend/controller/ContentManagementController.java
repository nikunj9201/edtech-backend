package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.ContentResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.TestSeries;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.VideoContent;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.ContentManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentManagementController {

    private final ContentManagementService contentManagementService;
    private final com.edtech.edtech_backend.service.StorageService fileStorageService;

    public ContentManagementController(ContentManagementService contentManagementService, com.edtech.edtech_backend.service.StorageService fileStorageService) {
        this.contentManagementService = contentManagementService;
        this.fileStorageService = fileStorageService;
    }

    // Admin Endpoints
    @PostMapping(path = "/videos", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoContent> uploadVideoMultipart(@ModelAttribute VideoUploadDto videoUploadDto, org.springframework.web.multipart.MultipartFile file) {
        // If called via multipart form, client should attach the file in field 'video' or 'file'.
        if (videoUploadDto.getVideo() == null && file != null && !file.isEmpty()) {
            videoUploadDto.setVideo(file);
        }
        // If a MultipartFile is present, store it using LocalFileStorageService and set storedFileName
        if (videoUploadDto.getVideo() != null && !videoUploadDto.getVideo().isEmpty()) {
            String stored = fileStorageService.storeFile(videoUploadDto.getVideo(), "videos");
            videoUploadDto.setStoredFileName(stored);
        }
        return ResponseEntity.ok(contentManagementService.createVideo(videoUploadDto));
    }

    // ... other admin endpoints

    // Student Endpoints
    @GetMapping("/videos/{courseId}")
    public ResponseEntity<List<ContentResponseDto<VideoContent>>> getVideos(@PathVariable Long courseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(contentManagementService.getVideosByCourse(courseId, user));
    }
}
