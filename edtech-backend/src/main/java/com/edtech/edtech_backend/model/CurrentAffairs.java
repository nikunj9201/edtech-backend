package com.edtech.edtech_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "current_affairs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CurrentAffairs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(name = "is_free", nullable = false)
    private boolean isFree = false;
}