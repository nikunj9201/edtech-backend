package com.edtech.edtech_backend.model;
import com.edtech.edtech_backend.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ebooks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "ebook_url", nullable = false)
    private String ebookUrl;

    @Column(name = "is_free", nullable = false)
    private boolean isFree = false;
}