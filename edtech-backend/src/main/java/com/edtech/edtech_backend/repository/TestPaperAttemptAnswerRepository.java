package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.TestPaperAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPaperAttemptAnswerRepository extends JpaRepository<TestPaperAttemptAnswer, Long> {
    List<TestPaperAttemptAnswer> findByAttemptId(Long attemptId);
}
