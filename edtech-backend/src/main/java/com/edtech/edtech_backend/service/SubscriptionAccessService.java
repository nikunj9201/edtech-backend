package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.ContentAccessType;

public interface SubscriptionAccessService {
    boolean hasActiveSubscription(User user);
    boolean canAccessContent(User user, ContentAccessType accessType);
}
