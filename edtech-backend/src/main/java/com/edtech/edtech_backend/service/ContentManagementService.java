package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.ContentResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.TestSeries;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.VideoContent;

import java.util.List;

public interface ContentManagementService {
    // Admin methods
    VideoContent createVideo(VideoUploadDto videoUploadDto);
    TestSeries createTestPaper(TestPaperUploadDto testPaperUploadDto);
    Quiz createQuiz(QuizUploadDto quizUploadDto);

    // Student methods
    List<ContentResponseDto<VideoContent>> getVideosByCourse(Long courseId, User user);
    List<ContentResponseDto<TestSeries>> getTestPapersByCourse(Long courseId, User user);
    List<ContentResponseDto<Quiz>> getQuizzesByCourse(Long courseId, User user);
}
