package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.ContentAccessType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "syllabuses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Syllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "syllabus_url", nullable = false)
    private String syllabusUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentAccessType accessType = ContentAccessType.FREE;
}
