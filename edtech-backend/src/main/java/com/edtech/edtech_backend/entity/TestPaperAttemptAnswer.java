package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_paper_attempt_answers")
@Data
public class TestPaperAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private TestPaperAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private TestPaperQuestion question;

    @Column(nullable = false)
    private String selectedAnswer;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false)
    private Boolean correct = false;

    @Column(columnDefinition = "TEXT")
    private String description;
}
