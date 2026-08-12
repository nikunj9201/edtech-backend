package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "video_content")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VideoContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "is_free_sample", nullable = false)
    private boolean isFreeSample = false;
}