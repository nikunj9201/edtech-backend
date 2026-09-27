package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.*;
import com.edtech.edtech_backend.entity.*;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.*;
import com.edtech.edtech_backend.service.SubscriptionAccessService;
import com.edtech.edtech_backend.service.TestSeriesService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestSeriesServiceImpl implements TestSeriesService {

    private final TestSeriesRepository testSeriesRepository;
    private final TestPaperRepository testPaperRepository;
    private final TestPaperQuestionRepository testPaperQuestionRepository;
    private final TestPaperAttemptRepository testPaperAttemptRepository;
    private final TestPaperAttemptAnswerRepository testPaperAttemptAnswerRepository;
    private final UserRepository userRepository;
    private final SubscriptionAccessService subscriptionAccessService;

    public TestSeriesServiceImpl(TestSeriesRepository testSeriesRepository,
                                TestPaperRepository testPaperRepository,
                                TestPaperQuestionRepository testPaperQuestionRepository,
                                TestPaperAttemptRepository testPaperAttemptRepository,
                                TestPaperAttemptAnswerRepository testPaperAttemptAnswerRepository,
                                UserRepository userRepository,
                                SubscriptionAccessService subscriptionAccessService) {
        this.testSeriesRepository = testSeriesRepository;
        this.testPaperRepository = testPaperRepository;
        this.testPaperQuestionRepository = testPaperQuestionRepository;
        this.testPaperAttemptRepository = testPaperAttemptRepository;
        this.testPaperAttemptAnswerRepository = testPaperAttemptAnswerRepository;
        this.userRepository = userRepository;
        this.subscriptionAccessService = subscriptionAccessService;
    }

    @Override
    public List<TestSeries> getAllTestSeries() {
        return testSeriesRepository.findAllByOrderByIdDesc();
    }

    @Override
    public TestSeries getTestSeriesById(Long id) {
        return testSeriesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test series not found"));
    }

    @Override
    public TestSeries createTestSeries(TestSeriesRequestDto dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test series title is required");
        }
        TestSeries series = new TestSeries();
        series.setTitle(dto.getTitle().trim());
        series.setSubtitle(dto.getSubtitle());
        series.setDescription(dto.getDescription());
        series.setThumbnailUrl(dto.getThumbnailUrl());
        series.setOriginalPrice(dto.getOriginalPrice());
        series.setDiscountedPrice(dto.getDiscountedPrice());
        series.setAccessType(dto.getAccessType() == null ? ContentAccessType.FREE : dto.getAccessType());
        series.setIsPremium(Boolean.TRUE.equals(dto.getIsPremium()) || series.getAccessType() == ContentAccessType.PREMIUM);
        series.setActive(dto.getActive() == null || dto.getActive());
        return testSeriesRepository.save(series);
    }

    @Override
    public TestSeries updateTestSeries(Long id, TestSeriesRequestDto dto) {
        TestSeries series = getTestSeriesById(id);
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            series.setTitle(dto.getTitle().trim());
        }
        if (dto.getSubtitle() != null) series.setSubtitle(dto.getSubtitle());
        if (dto.getDescription() != null) series.setDescription(dto.getDescription());
        if (dto.getThumbnailUrl() != null) series.setThumbnailUrl(dto.getThumbnailUrl());
        if (dto.getOriginalPrice() != null) series.setOriginalPrice(dto.getOriginalPrice());
        if (dto.getDiscountedPrice() != null) series.setDiscountedPrice(dto.getDiscountedPrice());
        if (dto.getAccessType() != null) {
            series.setAccessType(dto.getAccessType());
        }
        if (dto.getIsPremium() != null) {
            series.setIsPremium(dto.getIsPremium());
        }
        if (dto.getActive() != null) {
            series.setActive(dto.getActive());
        }
        series.setIsPremium(series.getAccessType() == ContentAccessType.PREMIUM || Boolean.TRUE.equals(series.getIsPremium()));
        return testSeriesRepository.save(series);
    }

    @Override
    public void deleteTestSeries(Long id) {
        TestSeries series = getTestSeriesById(id);
        series.setActive(false);
        testSeriesRepository.save(series);
    }

    @Override
    public List<TestPaper> getPapersByTestSeries(Long testSeriesId) {
        TestSeries series = getTestSeriesById(testSeriesId);
        return testPaperRepository.findByTestSeriesIdOrderByDisplayOrderAsc(series.getId());
    }

    @Override
    public TestPaper createTestPaper(Long testSeriesId, TestPaperRequestDto dto) {
        TestSeries series = getTestSeriesById(testSeriesId);
        TestPaper paper = new TestPaper();
        paper.setTestSeries(series);
        paper.setTitle(dto.getTitle());
        paper.setDescription(dto.getDescription());
        paper.setInstructions(dto.getInstructions());
        paper.setDurationMinutes(dto.getDurationMinutes() == null ? 0 : dto.getDurationMinutes());
        paper.setTotalMarks(dto.getTotalMarks() == null ? 0 : dto.getTotalMarks());
        paper.setPassingMarks(dto.getPassingMarks() == null ? 0 : dto.getPassingMarks());
        paper.setDisplayOrder(dto.getDisplayOrder() == null ? 0 : dto.getDisplayOrder());
        paper.setActive(dto.getActive() == null || dto.getActive());
        return testPaperRepository.save(paper);
    }

    @Override
    public TestPaper getTestPaperById(Long paperId) {
        return testPaperRepository.findById(paperId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test paper not found"));
    }

    @Override
    public TestPaper updateTestPaper(Long paperId, TestPaperRequestDto dto) {
        TestPaper paper = getTestPaperById(paperId);
        if (dto.getTitle() != null) paper.setTitle(dto.getTitle());
        if (dto.getDescription() != null) paper.setDescription(dto.getDescription());
        if (dto.getInstructions() != null) paper.setInstructions(dto.getInstructions());
        if (dto.getDurationMinutes() != null) paper.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getTotalMarks() != null) paper.setTotalMarks(dto.getTotalMarks());
        if (dto.getPassingMarks() != null) paper.setPassingMarks(dto.getPassingMarks());
        if (dto.getDisplayOrder() != null) paper.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getActive() != null) paper.setActive(dto.getActive());
        return testPaperRepository.save(paper);
    }

    @Override
    public void deleteTestPaper(Long paperId) {
        TestPaper paper = getTestPaperById(paperId);
        paper.setActive(false);
        testPaperRepository.save(paper);
    }

    @Override
    public List<TestPaperQuestion> getQuestionsByPaper(Long paperId) {
        getTestPaperById(paperId);
        return testPaperQuestionRepository.findByTestPaperIdOrderByDisplayOrderAsc(paperId);
    }

    @Override
    public TestPaperQuestion addQuestionToPaper(Long paperId, TestPaperQuestionDto dto) {
        TestPaper paper = getTestPaperById(paperId);
        if (dto == null || dto.getQuestionText() == null || dto.getQuestionText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question text is required");
        }
        TestPaperQuestion question = new TestPaperQuestion();
        question.setTestPaper(paper);
        question.setQuestionText(dto.getQuestionText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setDescription(dto.getDescription());
        question.setMarks(dto.getMarks() == null ? 1 : dto.getMarks());
        question.setNegativeMarks(dto.getNegativeMarks() == null ? 0.0 : dto.getNegativeMarks());
        question.setDisplayOrder(dto.getDisplayOrder() == null ? 0 : dto.getDisplayOrder());
        return testPaperQuestionRepository.save(question);
    }

    @Override
    public TestPaperQuestion updateQuestion(Long questionId, TestPaperQuestionDto dto) {
        TestPaperQuestion question = testPaperQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        if (dto.getQuestionText() != null) question.setQuestionText(dto.getQuestionText());
        if (dto.getOptionA() != null) question.setOptionA(dto.getOptionA());
        if (dto.getOptionB() != null) question.setOptionB(dto.getOptionB());
        if (dto.getOptionC() != null) question.setOptionC(dto.getOptionC());
        if (dto.getOptionD() != null) question.setOptionD(dto.getOptionD());
        if (dto.getCorrectAnswer() != null) question.setCorrectAnswer(dto.getCorrectAnswer());
        if (dto.getDescription() != null) question.setDescription(dto.getDescription());
        if (dto.getMarks() != null) question.setMarks(dto.getMarks());
        if (dto.getNegativeMarks() != null) question.setNegativeMarks(dto.getNegativeMarks());
        if (dto.getDisplayOrder() != null) question.setDisplayOrder(dto.getDisplayOrder());
        return testPaperQuestionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        TestPaperQuestion question = testPaperQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        TestPaper paper = question.getTestPaper();
        if (paper != null && paper.getQuestions() != null) {
            paper.getQuestions().removeIf(q -> q != null && q.getId() != null && q.getId().equals(questionId));
        }

        testPaperQuestionRepository.delete(question);
    }

    @Override
    public List<TestSeriesStudentDto> getAccessibleTestSeriesForStudent(User user) {
        List<TestSeries> all = testSeriesRepository.findByActiveTrueOrderByIdDesc();
        List<TestSeriesStudentDto> result = new ArrayList<>();
        for (TestSeries each : all) {
            TestSeriesStudentDto dto = new TestSeriesStudentDto();
            dto.setId(each.getId());
            dto.setTitle(each.getTitle());
            dto.setSubtitle(each.getSubtitle());
            dto.setDescription(each.getDescription());
            dto.setThumbnailUrl(each.getThumbnailUrl());
            dto.setOriginalPrice(each.getOriginalPrice());
            dto.setDiscountedPrice(each.getDiscountedPrice());
            dto.setAccessType(each.getAccessType());
            dto.setIsPremium(each.getIsPremium());
            dto.setLocked(each.getAccessType() == ContentAccessType.PREMIUM && !hasAccessToSeries(user, each));
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<TestPaperStudentDto> getAccessiblePapersForStudent(Long testSeriesId, User user) {
        TestSeries series = getTestSeriesById(testSeriesId);
        List<TestPaper> papers = testPaperRepository.findByTestSeriesIdAndActiveTrueOrderByDisplayOrderAsc(testSeriesId);
        List<TestPaperStudentDto> result = new ArrayList<>();
        boolean locked = series.getAccessType() == ContentAccessType.PREMIUM && !hasAccessToSeries(user, series);
        for (TestPaper paper : papers) {
            TestPaperStudentDto dto = new TestPaperStudentDto();
            dto.setId(paper.getId());
            dto.setTitle(paper.getTitle());
            dto.setDescription(paper.getDescription());
            dto.setDurationMinutes(paper.getDurationMinutes());
            dto.setTotalMarks(paper.getTotalMarks());
            dto.setTotalQuestions(testPaperQuestionRepository.findByTestPaperIdOrderByDisplayOrderAsc(paper.getId()).size());
            dto.setLocked(locked);
            result.add(dto);
        }
        return result;
    }

    @Override
    public boolean hasAccessToSeries(User user, TestSeries testSeries) {
        if (testSeries == null) return true;
        if (testSeries.getAccessType() == ContentAccessType.FREE) return true;
        if (user == null || user.getId() == null) return false;
        User persistedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return subscriptionAccessService.hasActiveSubscription(persistedUser);
    }

    @Override
    @Transactional
    public TestPaperSubmissionResultDto startPaper(Long paperId, User user) {
        TestPaper paper = getTestPaperById(paperId);
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        if (!hasAccessToSeries(user, paper.getTestSeries())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this paper.");
        }
        TestPaperSubmissionResultDto result = new TestPaperSubmissionResultDto();
        result.setPaperId(paperId);
        result.setTotalQuestions(testPaperQuestionRepository.findByTestPaperIdOrderByDisplayOrderAsc(paperId).size());
        result.setTotalMarks(paper.getTotalMarks() == null ? 0 : paper.getTotalMarks());
        result.setObtainedMarks(0);
        result.setPercentage(0.0);
        return result;
    }

    @Override
    @Transactional
    public TestPaperSubmissionResultDto submitPaper(Long paperId, User user, List<TestPaperSubmissionAnswerDto> answers) {
        TestPaper paper = getTestPaperById(paperId);
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        if (!hasAccessToSeries(user, paper.getTestSeries())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this paper.");
        }

        User persistedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<TestPaperQuestion> questions = testPaperQuestionRepository.findByTestPaperIdOrderByDisplayOrderAsc(paperId);
        Map<Long, String> selectedByQuestionId = new HashMap<>();
        if (answers != null) {
            for (TestPaperSubmissionAnswerDto answer : answers) {
                if (answer != null && answer.getQuestionId() != null && answer.getSelectedAnswer() != null) {
                    selectedByQuestionId.put(answer.getQuestionId(), answer.getSelectedAnswer().trim().toUpperCase());
                }
            }
        }

        int totalQuestions = questions.size();
        int attemptedQuestions = selectedByQuestionId.size();
        int correctAnswers = 0;
        int wrongAnswers = 0;
        int unattemptedQuestions = Math.max(0, totalQuestions - attemptedQuestions);
        int totalMarks = paper.getTotalMarks() == null ? 0 : paper.getTotalMarks();
        int obtainedMarks = 0;

        TestPaperAttempt attempt = new TestPaperAttempt();
        attempt.setStudent(persistedUser);
        attempt.setTestPaper(paper);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(0);
        attempt.setWrongAnswers(0);
        attempt.setUnattemptedQuestions(unattemptedQuestions);
        attempt.setObtainedMarks(0);
        attempt.setPercentage(0.0);
        attempt.setStatus("SUBMITTED");
        attempt = testPaperAttemptRepository.save(attempt);

        TestPaperSubmissionResultDto result = new TestPaperSubmissionResultDto();
        result.setPaperId(paperId);
        result.setTotalQuestions(totalQuestions);
        result.setAttemptedQuestions(attemptedQuestions);
        result.setCorrectAnswers(0);
        result.setWrongAnswers(0);
        result.setUnattemptedQuestions(unattemptedQuestions);
        result.setTotalMarks(totalMarks);
        result.setObtainedMarks(0);
        result.setPercentage(0.0);

        for (TestPaperQuestion question : questions) {
            String selectedAnswer = selectedByQuestionId.get(question.getId());
            if (selectedAnswer == null) {
                continue;
            }
            String correctAnswer = question.getCorrectAnswer() == null ? "" : question.getCorrectAnswer().trim().toUpperCase();
            boolean isCorrect = correctAnswer.equalsIgnoreCase(selectedAnswer);
            if (isCorrect) {
                correctAnswers++;
                obtainedMarks += question.getMarks() == null ? 1 : question.getMarks();
            } else {
                wrongAnswers++;
            }

            TestPaperAttemptAnswer answer = new TestPaperAttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedAnswer(selectedAnswer);
            answer.setCorrectAnswer(correctAnswer);
            answer.setCorrect(isCorrect);
            answer.setDescription(question.getDescription());
            testPaperAttemptAnswerRepository.save(answer);

            TestPaperQuestionResultDto qResult = new TestPaperQuestionResultDto();
            qResult.setQuestionId(question.getId());
            qResult.setSelectedAnswer(selectedAnswer);
            qResult.setCorrectAnswer(correctAnswer);
            qResult.setCorrect(isCorrect);
            qResult.setDescription(question.getDescription());
            result.getQuestions().add(qResult);
        }

        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setObtainedMarks(obtainedMarks);
        result.setPercentage(totalQuestions == 0 ? 0.0 : (obtainedMarks * 100.0 / totalMarks == 0 ? 0.0 : (obtainedMarks * 100.0 / totalMarks)));

        attempt.setCorrectAnswers(correctAnswers);
        attempt.setWrongAnswers(wrongAnswers);
        attempt.setUnattemptedQuestions(unattemptedQuestions);
        attempt.setObtainedMarks(obtainedMarks);
        attempt.setPercentage(result.getPercentage());
        testPaperAttemptRepository.save(attempt);

        return result;
    }
}
