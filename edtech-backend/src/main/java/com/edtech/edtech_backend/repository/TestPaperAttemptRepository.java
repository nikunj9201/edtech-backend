package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.TestPaperAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPaperAttemptRepository extends JpaRepository<TestPaperAttempt, Long> {
    List<TestPaperAttempt> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    List<TestPaperAttempt> findByStudentIdAndTestPaperIdOrderBySubmittedAtDesc(Long studentId, Long testPaperId);
}
