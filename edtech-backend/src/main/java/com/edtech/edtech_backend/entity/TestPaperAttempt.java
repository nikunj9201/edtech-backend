package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_paper_attempts")
@Data
public class TestPaperAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_paper_id", nullable = false)
    private TestPaper testPaper;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer wrongAnswers;

    @Column(nullable = false)
    private Integer unattemptedQuestions;

    @Column(nullable = false)
    private Integer obtainedMarks;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private String status = "SUBMITTED";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
