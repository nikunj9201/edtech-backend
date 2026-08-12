package com.edtech.edtech_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String type;

    private boolean isPremium;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String fileUrl;
}