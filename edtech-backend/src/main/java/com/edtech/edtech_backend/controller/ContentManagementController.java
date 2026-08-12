package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.model.Quiz;
import com.edtech.edtech_backend.model.TestSeries;
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

    public ContentManagementController(ContentManagementService contentManagementService) {
        this.contentManagementService = contentManagementService;
    }

    // Admin Endpoints
    @PostMapping("/videos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoContent> uploadVideo(@RequestBody VideoUploadDto videoUploadDto) {
        return ResponseEntity.ok(contentManagementService.createVideo(videoUploadDto));
    }

    @PostMapping("/test-papers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestSeries> uploadTestPaper(@RequestBody TestPaperUploadDto testPaperUploadDto) {
        return ResponseEntity.ok(contentManagementService.createTestPaper(testPaperUploadDto));
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> uploadQuiz(@RequestBody QuizUploadDto quizUploadDto) {
        return ResponseEntity.ok(contentManagementService.createQuiz(quizUploadDto));
    }

    // Student Endpoints
    @GetMapping("/videos/{courseId}")
    public ResponseEntity<List<VideoContent>> getVideos(@PathVariable Long courseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contentManagementService.getVideosByCourse(courseId, userDetails.getId()));
    }

    @GetMapping("/test-papers/{courseId}")
    public ResponseEntity<List<TestSeries>> getTestPapers(@PathVariable Long courseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contentManagementService.getTestPapersByCourse(courseId, userDetails.getId()));
    }

    @GetMapping("/quizzes/{courseId}")
    public ResponseEntity<List<Quiz>> getQuizzes(@PathVariable Long courseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contentManagementService.getQuizzesByCourse(courseId, userDetails.getId()));
    }
}