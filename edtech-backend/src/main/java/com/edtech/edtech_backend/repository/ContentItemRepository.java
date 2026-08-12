package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem, Long> {
    List<ContentItem> findByCategoryId(Long categoryId);
    List<ContentItem> findByCategoryIdAndIsPremium(Long categoryId, boolean isPremium);
}