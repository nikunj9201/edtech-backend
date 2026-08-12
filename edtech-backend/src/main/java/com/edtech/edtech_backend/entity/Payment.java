package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan plan;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
