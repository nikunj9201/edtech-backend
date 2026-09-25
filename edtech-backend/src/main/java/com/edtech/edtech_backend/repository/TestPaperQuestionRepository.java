package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.TestPaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPaperQuestionRepository extends JpaRepository<TestPaperQuestion, Long> {
    List<TestPaperQuestion> findByTestPaperIdOrderByDisplayOrderAsc(Long testPaperId);
}
