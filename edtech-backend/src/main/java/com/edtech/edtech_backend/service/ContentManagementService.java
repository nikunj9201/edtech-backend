package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.model.Quiz;
import com.edtech.edtech_backend.model.TestSeries;
import com.edtech.edtech_backend.entity.VideoContent;

import java.util.List;

public interface ContentManagementService {
    // Admin methods
    VideoContent createVideo(VideoUploadDto videoUploadDto);
    TestSeries createTestPaper(TestPaperUploadDto testPaperUploadDto);
    Quiz createQuiz(QuizUploadDto quizUploadDto);

    // Student methods
    List<VideoContent> getVideosByCourse(Long courseId, Long userId);
    List<TestSeries> getTestPapersByCourse(Long courseId, Long userId);
    List<Quiz> getQuizzesByCourse(Long courseId, Long userId);
}