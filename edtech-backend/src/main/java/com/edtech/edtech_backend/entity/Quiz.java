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

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentAccessType accessType = ContentAccessType.FREE;
}
