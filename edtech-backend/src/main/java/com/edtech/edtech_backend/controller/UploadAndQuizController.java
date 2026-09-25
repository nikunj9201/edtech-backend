package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.QuizQuestionDto;
import com.edtech.edtech_backend.dto.QuizQuestionResultDto;
import com.edtech.edtech_backend.dto.QuizSubmissionDto;
import com.edtech.edtech_backend.dto.QuizSubmissionResponseDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.VideoContent;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.QuizQuestionRepository;
import com.edtech.edtech_backend.repository.QuizRepository;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.ContentManagementService;
import com.edtech.edtech_backend.service.SubscriptionAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class UploadAndQuizController {

    private final ContentManagementService contentManagementService;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final com.edtech.edtech_backend.service.StorageService fileStorageService;
    private final SubscriptionAccessService subscriptionAccessService;

    public UploadAndQuizController(ContentManagementService contentManagementService,
                                   QuizRepository quizRepository,
                                   QuizQuestionRepository quizQuestionRepository,
                                   com.edtech.edtech_backend.service.StorageService fileStorageService,
                                   SubscriptionAccessService subscriptionAccessService) {
        this.contentManagementService = contentManagementService;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.fileStorageService = fileStorageService;
        this.subscriptionAccessService = subscriptionAccessService;
    }

    @PostMapping("/videos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoContent> uploadVideoMultipart(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("title") String title,
                                                             @RequestParam("courseId") Long courseId,
                                                             @RequestParam(value = "accessType", required = false) ContentAccessType accessType) throws IOException {
        if (accessType == null) accessType = ContentAccessType.FREE;

        String stored = fileStorageService.storeFile(file, "videos");

        VideoUploadDto dto = new VideoUploadDto();
        dto.setTitle(title);
        dto.setCourseId(courseId);
        dto.setStoredFileName(stored);
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
            q.setDescription(qdto.getDescription());
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
            q.setCorrectOptionIndex(i % 4);
            q.setDescription("Correct option is " + (char) ('A' + (i % 4)) + " because it is the right answer.");
            created.add(quizQuestionRepository.save(q));
        }
        return ResponseEntity.ok(created);
    }

    @PostMapping("/quizzes/{quizId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<QuizSubmissionResponseDto> submitQuiz(@PathVariable Long quizId,
                                                             @RequestBody QuizSubmissionDto submission,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Course course = quiz.getCourse();
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is not mapped to a course");
        }

        User currentUser = new User();
        currentUser.setId(userDetails.getId());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && course.getAccessType() == ContentAccessType.PREMIUM && !subscriptionAccessService.hasAccessToCourse(currentUser, course)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this course.");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
        Map<Long, Integer> answers = submission != null ? submission.getNormalizedAnswers() : Map.of();
        int total = questions.size();
        int correct = 0;
        int wrong = 0;
        int attempted = 0;
        int unattempted = 0;
        QuizSubmissionResponseDto resp = new QuizSubmissionResponseDto();
        resp.setQuizId(quizId);
        resp.setTotalQuestions(total);
        resp.setQuestions(new ArrayList<>());

        for (QuizQuestion q : questions) {
            Integer selected = answers.get(q.getId());
            if (selected == null) {
                unattempted++;
                continue;
            }
            attempted++;
            boolean isCorrect = selected.equals(q.getCorrectOptionIndex());
            if (isCorrect) {
                correct++;
            } else {
                wrong++;
            }

            String selectedAnswer = optionIndexToLabel(selected);
            String correctAnswer = optionIndexToLabel(q.getCorrectOptionIndex());
            QuizQuestionResultDto result = new QuizQuestionResultDto();
            result.setQuestionId(q.getId());
            result.setSelectedAnswer(selectedAnswer);
            result.setCorrectAnswer(correctAnswer);
            result.setCorrect(isCorrect);
            result.setDescription(q.getDescription());
            resp.getQuestions().add(result);
        }

        resp.setAttemptedQuestions(attempted);
        resp.setCorrectAnswers(correct);
        resp.setWrongAnswers(wrong);
        resp.setUnattemptedQuestions(unattempted);
        resp.setTotalMarks(total);
        resp.setMarksObtained(correct);
        resp.setObtainedMarks(correct);
        resp.setPercentage(total == 0 ? 0.0 : (correct * 100.0 / total));
        return ResponseEntity.ok(resp);
    }

    private String optionIndexToLabel(Integer optionIndex) {
        if (optionIndex == null) {
            return null;
        }
        if (optionIndex == 0) return "A";
        if (optionIndex == 1) return "B";
        if (optionIndex == 2) return "C";
        if (optionIndex == 3) return "D";
        return String.valueOf(optionIndex);
    }
}
