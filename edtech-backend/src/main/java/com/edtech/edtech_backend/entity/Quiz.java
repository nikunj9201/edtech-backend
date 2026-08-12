package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.ContentAccessType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentAccessType accessType = ContentAccessType.FREE;
}
