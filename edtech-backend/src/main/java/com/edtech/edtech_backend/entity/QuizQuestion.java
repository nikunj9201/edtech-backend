package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

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

    // 0 = A, 1 = B, 2 = C, 3 = D
    @Column(nullable = false)
    private Integer correctOptionIndex;
}
