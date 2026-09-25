package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.*;
import com.edtech.edtech_backend.entity.TestPaper;
import com.edtech.edtech_backend.entity.TestPaperQuestion;
import com.edtech.edtech_backend.entity.TestSeries;
import com.edtech.edtech_backend.entity.User;

import java.util.List;

public interface TestSeriesService {
    List<TestSeries> getAllTestSeries();
    TestSeries getTestSeriesById(Long id);
    TestSeries createTestSeries(TestSeriesRequestDto dto);
    TestSeries updateTestSeries(Long id, TestSeriesRequestDto dto);
    void deleteTestSeries(Long id);

    List<TestPaper> getPapersByTestSeries(Long testSeriesId);
    TestPaper createTestPaper(Long testSeriesId, TestPaperRequestDto dto);
    TestPaper getTestPaperById(Long paperId);
    TestPaper updateTestPaper(Long paperId, TestPaperRequestDto dto);
    void deleteTestPaper(Long paperId);

    List<TestPaperQuestion> getQuestionsByPaper(Long paperId);
    TestPaperQuestion addQuestionToPaper(Long paperId, TestPaperQuestionDto dto);
    TestPaperQuestion updateQuestion(Long questionId, TestPaperQuestionDto dto);
    void deleteQuestion(Long questionId);

    List<TestSeriesStudentDto> getAccessibleTestSeriesForStudent(User user);
    List<TestPaperStudentDto> getAccessiblePapersForStudent(Long testSeriesId, User user);
    boolean hasAccessToSeries(User user, TestSeries testSeries);

    TestPaperSubmissionResultDto startPaper(Long paperId, User user);
    TestPaperSubmissionResultDto submitPaper(Long paperId, User user, List<TestPaperSubmissionAnswerDto> answers);
}
