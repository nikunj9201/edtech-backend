package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.VideoContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoContentRepository extends JpaRepository<VideoContent, Long> {
    List<VideoContent> findByCourseId(Long courseId);
}