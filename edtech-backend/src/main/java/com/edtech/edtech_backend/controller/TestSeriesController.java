package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.*;
import com.edtech.edtech_backend.entity.TestPaper;
import com.edtech.edtech_backend.entity.TestPaperQuestion;
import com.edtech.edtech_backend.entity.TestSeries;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.TestSeriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class TestSeriesController {

    private final TestSeriesService testSeriesService;

    public TestSeriesController(TestSeriesService testSeriesService) {
        this.testSeriesService = testSeriesService;
    }

    @PostMapping("/test-series")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestSeries> createTestSeries(@Valid @RequestBody TestSeriesRequestDto dto) {
        return ResponseEntity.ok(testSeriesService.createTestSeries(dto));
    }

    @GetMapping("/test-series")
        public ResponseEntity<List<TestSeriesStudentDto>> getAllTestSeries(@AuthenticationPrincipal UserDetailsImpl userDetails) {
            User user = userDetails == null ? null : new User();
            if (user != null) {
                user.setId(userDetails.getId());
            }
            return ResponseEntity.ok(testSeriesService.getAccessibleTestSeriesForStudent(user));
        }

    @GetMapping("/test-series/{id}")
    public ResponseEntity<TestSeries> getTestSeriesById(@PathVariable Long id) {
        return ResponseEntity.ok(testSeriesService.getTestSeriesById(id));
    }

    @PutMapping("/test-series/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestSeries> updateTestSeries(@PathVariable Long id, @Valid @RequestBody TestSeriesRequestDto dto) {
        return ResponseEntity.ok(testSeriesService.updateTestSeries(id, dto));
    }

    @DeleteMapping("/test-series/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTestSeries(@PathVariable Long id) {
        testSeriesService.deleteTestSeries(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-series/{testSeriesId}/papers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestPaper> createTestPaper(@PathVariable Long testSeriesId, @Valid @RequestBody TestPaperRequestDto dto) {
        return ResponseEntity.ok(testSeriesService.createTestPaper(testSeriesId, dto));
    }

    @GetMapping("/test-series/{testSeriesId}/papers")
    public ResponseEntity<List<TestPaperStudentDto>> getPapersForSeries(@PathVariable Long testSeriesId,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails == null ? null : new User();
        if (user != null) {
            user.setId(userDetails.getId());
        }
        return ResponseEntity.ok(testSeriesService.getAccessiblePapersForStudent(testSeriesId, user));
    }

    @GetMapping("/test-papers/{paperId}")
        public ResponseEntity<?> getPaper(@PathVariable Long paperId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        TestPaper paper = testSeriesService.getTestPaperById(paperId);
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                User user = userDetails == null ? null : new User();
                if (user != null) user.setId(userDetails.getId());
                if (!testSeriesService.hasAccessToSeries(user, paper.getTestSeries())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this test series.");
                }
                TestPaperStudentDto dto = new TestPaperStudentDto();
                dto.setId(paper.getId());
                dto.setTitle(paper.getTitle());
                dto.setDescription(paper.getDescription());
                dto.setDurationMinutes(paper.getDurationMinutes());
                dto.setTotalMarks(paper.getTotalMarks());
                dto.setTotalQuestions(paper.getQuestions() == null ? 0 : paper.getQuestions().size());
                dto.setLocked(!testSeriesService.hasAccessToSeries(user, paper.getTestSeries()));
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.ok(paper);
        }

    @PutMapping("/test-papers/{paperId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestPaper> updateTestPaper(@PathVariable Long paperId, @Valid @RequestBody TestPaperRequestDto dto) {
        return ResponseEntity.ok(testSeriesService.updateTestPaper(paperId, dto));
    }

    @DeleteMapping("/test-papers/{paperId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTestPaper(@PathVariable Long paperId) {
        testSeriesService.deleteTestPaper(paperId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-papers/{paperId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestPaperQuestion> addQuestion(@PathVariable Long paperId, @Valid @RequestBody TestPaperQuestionDto dto) {
        return ResponseEntity.ok(testSeriesService.addQuestionToPaper(paperId, dto));
    }

    @GetMapping("/test-papers/{paperId}/questions")
    public ResponseEntity<List<TestPaperQuestionDto>> getQuestionsByPaper(@PathVariable Long paperId,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
            TestPaper paper = testSeriesService.getTestPaperById(paperId);
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            User user = userDetails == null ? null : new User();
            if (user != null) user.setId(userDetails.getId());
            if (!isAdmin) {
                if (!testSeriesService.hasAccessToSeries(user, paper.getTestSeries())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this test series.");
                }
            }
            List<TestPaperQuestion> questions = testSeriesService.getQuestionsByPaper(paperId);
            List<TestPaperQuestionDto> dtoList = new ArrayList<>();
            for (TestPaperQuestion q : questions) {
                TestPaperQuestionDto dto = toQuestionDto(q);
                if (!isAdmin) {
                    dto.setCorrectAnswer(null);
                    dto.setDescription(null);
                }
                dtoList.add(dto);
            }
            return ResponseEntity.ok(dtoList);
        }

    @PutMapping("/test-paper-questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestPaperQuestion> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody TestPaperQuestionDto dto) {
        return ResponseEntity.ok(testSeriesService.updateQuestion(questionId, dto));
    }

    @DeleteMapping("/test-paper-questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        testSeriesService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test-papers/{paperId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<TestPaperSubmissionResultDto> submitPaper(@PathVariable Long paperId,
                                                                   @RequestBody TestPaperSubmissionRequestDto request,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(testSeriesService.submitPaper(paperId, user, request == null ? List.of() : request.getAnswers()));
    }

    private TestPaperQuestionDto toQuestionDto(TestPaperQuestion question) {
        TestPaperQuestionDto dto = new TestPaperQuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setDescription(question.getDescription());
        dto.setMarks(question.getMarks());
        dto.setNegativeMarks(question.getNegativeMarks());
        dto.setDisplayOrder(question.getDisplayOrder());
        return dto;
    }
}
