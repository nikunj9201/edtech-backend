package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class CoursePurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal courseAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseRequestStatus status = PurchaseRequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "approved_by_admin_id")
    private User approvedByAdmin;

    private LocalDateTime approvedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
