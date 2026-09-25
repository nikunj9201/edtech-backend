package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.QuizResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.CourseRepository;
import com.edtech.edtech_backend.repository.QuizRepository;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.QuizService;
import com.edtech.edtech_backend.service.SubscriptionAccessService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final SubscriptionAccessService subscriptionAccessService;

    public QuizController(QuizService quizService,
                          QuizRepository quizRepository,
                          CourseRepository courseRepository,
                          SubscriptionAccessService subscriptionAccessService) {
        this.quizService = quizService;
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.subscriptionAccessService = subscriptionAccessService;
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

    @GetMapping("/quizzes/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<Quiz>> getQuizzesByCourse(@PathVariable Long courseId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Quiz course request: name={}, authorities={}, authenticated={}",
                authentication != null ? authentication.getName() : "null",
                authentication != null ? authentication.getAuthorities() : "[]",
                authentication != null && authentication.isAuthenticated());

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && course.getAccessType() == ContentAccessType.PREMIUM) {
            User currentUser = new User();
            currentUser.setId(userDetails.getId());

            if (!subscriptionAccessService.hasAccessToCourse(currentUser, course)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this course.");
            }
        }

        return ResponseEntity.ok(quizRepository.findByCourseId(courseId));
    }

    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<QuizResponseDto> getQuizById(@PathVariable Long quizId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        QuizResponseDto dto = quizService.getQuizById(quizId);

        // If admin, allow
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return ResponseEntity.ok(dto);

        // Student access check
        if (userDetails == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (course.getAccessType() == ContentAccessType.FREE) {
            return ResponseEntity.ok(dto);
        }

        User currentUser = new User();
        currentUser.setId(userDetails.getId());
        if (!subscriptionAccessService.hasAccessToCourse(currentUser, course)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this course.");
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
        public ResponseEntity<?> getQuizQuestions(@PathVariable Long quizId,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            if (userDetails == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
            Course course = quiz.getCourse();
            if (course == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz is not mapped to a course");

            if (course.getAccessType() == ContentAccessType.PREMIUM) {
                User currentUser = new User();
                currentUser.setId(userDetails.getId());
                if (!subscriptionAccessService.hasAccessToCourse(currentUser, course)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this course.");
                }
            }
                // return sanitized questions for students (do not expose correct option or description)
                List<QuizQuestion> questions = quizService.getQuestionsByQuizId(quizId);
                java.util.List<com.edtech.edtech_backend.dto.QuizQuestionStudentDto> dtoList = new java.util.ArrayList<>();
                for (QuizQuestion q : questions) {
                    com.edtech.edtech_backend.dto.QuizQuestionStudentDto dto = new com.edtech.edtech_backend.dto.QuizQuestionStudentDto();
                    dto.setId(q.getId());
                    dto.setQuestionText(q.getQuestionText());
                    dto.setOptionA(q.getOptionA());
                    dto.setOptionB(q.getOptionB());
                    dto.setOptionC(q.getOptionC());
                    dto.setOptionD(q.getOptionD());
                    dtoList.add(dto);
                }
                return ResponseEntity.ok(dtoList);
            }

            // admin: return full entities
            return ResponseEntity.ok(quizService.getQuestionsByQuizId(quizId));
        }
}
