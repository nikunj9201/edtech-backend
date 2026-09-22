package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.QuizResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;

import java.util.List;

public interface QuizService {
    Quiz createQuiz(QuizUploadDto quizUploadDto);
    QuizResponseDto getQuizById(Long quizId);
    List<QuizQuestion> getQuestionsByQuizId(Long quizId);
}
