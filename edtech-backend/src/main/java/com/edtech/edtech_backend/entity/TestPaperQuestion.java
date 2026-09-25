package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_paper_questions")
@Data
public class TestPaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_paper_id", nullable = false)
    private TestPaper testPaper;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer marks = 1;

    @Column(nullable = false)
    private Double negativeMarks = 0.0;

    @Column(nullable = false)
    private Integer displayOrder;
}
