package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.Quiz; // Corrected import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);
}
