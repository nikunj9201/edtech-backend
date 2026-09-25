package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.ContentAccessType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_series")
@Data
public class TestSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 1024)
    private String thumbnailUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentAccessType accessType = ContentAccessType.FREE;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
