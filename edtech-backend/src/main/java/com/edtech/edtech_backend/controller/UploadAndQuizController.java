package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.QuizQuestionDto;
import com.edtech.edtech_backend.dto.QuizSubmissionDto;
import com.edtech.edtech_backend.dto.QuizSubmissionResponseDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;
import com.edtech.edtech_backend.entity.VideoContent;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.QuizQuestionRepository;
import com.edtech.edtech_backend.repository.QuizRepository;
import com.edtech.edtech_backend.service.ContentManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class UploadAndQuizController {

    private final ContentManagementService contentManagementService;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public UploadAndQuizController(ContentManagementService contentManagementService,
                                   QuizRepository quizRepository,
                                   QuizQuestionRepository quizQuestionRepository) {
        this.contentManagementService = contentManagementService;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @PostMapping("/videos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoContent> uploadVideoMultipart(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("title") String title,
                                                             @RequestParam("courseId") Long courseId,
                                                             @RequestParam(value = "accessType", required = false) ContentAccessType accessType) throws IOException {
        if (accessType == null) accessType = ContentAccessType.FREE;

        Path uploadDir = Paths.get("uploads", "videos").toAbsolutePath();
        Files.createDirectories(uploadDir);

        String original = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("[^a-zA-Z0-9.\\-_]", "_") : "video.mp4");
        Path target = uploadDir.resolve(filename);
        file.transferTo(target.toFile());

        // Save DB record using existing service (pass URL that the app will serve)
        VideoUploadDto dto = new VideoUploadDto();
        dto.setTitle(title);
        dto.setCourseId(courseId);
        dto.setVideoUrl("/uploads/videos/" + filename);
        dto.setAccessType(accessType);

        VideoContent saved = contentManagementService.createVideo(dto);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizQuestion>> addQuestions(@PathVariable Long quizId, @RequestBody List<QuizQuestionDto> questions) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<QuizQuestion> saved = new ArrayList<>();
        for (QuizQuestionDto qdto : questions) {
            QuizQuestion q = new QuizQuestion();
            q.setQuiz(quiz);
            q.setQuestionText(qdto.getQuestionText());
            q.setOptionA(qdto.getOptionA());
            q.setOptionB(qdto.getOptionB());
            q.setOptionC(qdto.getOptionC());
            q.setOptionD(qdto.getOptionD());
            q.setCorrectOptionIndex(qdto.getCorrectOptionIndex());
            saved.add(quizQuestionRepository.save(q));
        }
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/quizzes/{quizId}/generate-sample")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizQuestion>> generateSampleQuestions(@PathVariable Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<QuizQuestion> created = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            QuizQuestion q = new QuizQuestion();
            q.setQuiz(quiz);
            q.setQuestionText("Sample question " + i + " for quiz " + quiz.getTitle());
            q.setOptionA("Option A");
            q.setOptionB("Option B");
            q.setOptionC("Option C");
            q.setOptionD("Option D");
            q.setCorrectOptionIndex(i % 4); // rotate correct option
            created.add(quizQuestionRepository.save(q));
        }
        return ResponseEntity.ok(created);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    public ResponseEntity<QuizSubmissionResponseDto> submitQuiz(@PathVariable Long quizId, @RequestBody QuizSubmissionDto submission) {
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
        Map<Long, Integer> answers = submission.getAnswers();
        int total = questions.size();
        int correct = 0;
        for (QuizQuestion q : questions) {
            Integer selected = answers != null ? answers.get(q.getId()) : null;
            if (selected != null && selected.equals(q.getCorrectOptionIndex())) correct++;
        }
        QuizSubmissionResponseDto resp = new QuizSubmissionResponseDto();
        resp.setTotalQuestions(total);
        resp.setCorrectAnswers(correct);
        resp.setMarksObtained(correct); // 1 mark per question
        resp.setPercentage(total == 0 ? 0.0 : (correct * 100.0 / total));
        return ResponseEntity.ok(resp);
    }
}
