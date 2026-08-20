package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.SubscriptionActivationRequest;
import com.edtech.edtech_backend.dto.SubscriptionPlanDto;
import com.edtech.edtech_backend.dto.UserSubscriptionDto;
import com.edtech.edtech_backend.entity.SubscriptionPlan;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserSubscription;

import java.util.List;

public interface SubscriptionService {
    // Plan management
    List<SubscriptionPlan> getAllPlans();
    SubscriptionPlan createPlan(SubscriptionPlanDto planDto);

    // User subscription management
    UserSubscriptionDto purchaseSubscription(User user, Long planId, String paymentMethod);
    UserSubscriptionDto getMySubscription(User user);
    List<UserSubscriptionDto> getSubscriptionHistory(User user);
    UserSubscription activateSubscription(SubscriptionActivationRequest request, User admin);
}
