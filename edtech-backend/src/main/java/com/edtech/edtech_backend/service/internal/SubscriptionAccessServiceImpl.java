package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserSubscription;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import com.edtech.edtech_backend.repository.UserSubscriptionRepository;
import com.edtech.edtech_backend.service.SubscriptionAccessService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionAccessServiceImpl implements SubscriptionAccessService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    public SubscriptionAccessServiceImpl(UserSubscriptionRepository userSubscriptionRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Override
    public boolean hasActiveSubscription(User user) {
        Optional<UserSubscription> activeSubscription = userSubscriptionRepository
                .findByUserAndStatus(user, SubscriptionStatus.ACTIVE);

        if (activeSubscription.isEmpty()) {
            return false;
        }

        // Request-time validation: check if the subscription has expired
        UserSubscription subscription = activeSubscription.get();
        if (subscription.getEndDate().isBefore(LocalDate.now())) {
            // If expired, update the status in the database
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);
            return false;
        }

        return true;
    }

    @Override
    public boolean canAccessContent(User user, ContentAccessType accessType) {
        if (accessType == ContentAccessType.FREE) {
            return true; // Free content is always accessible
        }
        // For premium content, check for an active subscription
        return hasActiveSubscription(user);
    }
}
