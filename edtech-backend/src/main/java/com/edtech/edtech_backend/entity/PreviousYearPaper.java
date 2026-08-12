package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.ContentAccessType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "previous_year_papers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PreviousYearPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "paper_url", nullable = false)
    private String paperUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentAccessType accessType = ContentAccessType.FREE;
}
