package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.QuizResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;
import com.edtech.edtech_backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponseDto> createQuiz(@Valid @RequestBody QuizUploadDto request) {
        Quiz createdQuiz = quizService.createQuiz(request);
        QuizResponseDto response = new QuizResponseDto(
                createdQuiz.getId(),
                createdQuiz.getTitle(),
                createdQuiz.getDescription(),
                createdQuiz.getCourse() != null ? createdQuiz.getCourse().getId() : null,
                createdQuiz.getCategory() != null ? createdQuiz.getCategory().getId() : null,
                createdQuiz.getAccessType(),
                0
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponseDto> getQuizById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    @GetMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizQuestion>> getQuizQuestions(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsByQuizId(quizId));
    }
}
