package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.ContentItemDTO;
import com.edtech.edtech_backend.entity.ContentItem;
import com.edtech.edtech_backend.entity.Subscription;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.repository.ContentItemRepository;
import com.edtech.edtech_backend.repository.SubscriptionRepository;
import com.edtech.edtech_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentItemRepository contentItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public List<ContentItemDTO> getContentByCategoryId(Long categoryId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isSubscribed = isUserSubscribed(user);

        List<ContentItem> contentItems;
        if (isSubscribed) {
            contentItems = contentItemRepository.findByCategoryId(categoryId);
        } else {
            contentItems = contentItemRepository.findByCategoryIdAndIsPremium(categoryId, false);
        }

        return contentItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean isUserSubscribed(User user) {
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);
        return subscriptions.stream()
                .anyMatch(sub -> sub.isActive() && sub.getEndDate().isAfter(LocalDate.now()));
    }

    private ContentItemDTO convertToDto(ContentItem contentItem) {
        return new ContentItemDTO(
                contentItem.getId(),
                contentItem.getTitle(),
                contentItem.getType(),
                contentItem.isPremium(),
                contentItem.getCategory().getId(),
                contentItem.getFileUrl()
        );
    }
}