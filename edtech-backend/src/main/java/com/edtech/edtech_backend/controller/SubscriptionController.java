package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.PurchaseRequestDto;
import com.edtech.edtech_backend.dto.SubscriptionActivationRequest;
import com.edtech.edtech_backend.dto.SubscriptionPlanDto;
import com.edtech.edtech_backend.dto.UserSubscriptionDto;
import com.edtech.edtech_backend.entity.SubscriptionPlan;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserSubscription;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Public API to view plans
    @GetMapping("/subscriptions/plans")
    public ResponseEntity<List<SubscriptionPlan>> getActivePlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    // Admin API to create a plan
    @PostMapping("/admin/subscriptions/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlanDto planDto) {
        return ResponseEntity.ok(subscriptionService.createPlan(planDto));
    }

    // Admin API to activate a subscription
    @PostMapping("/admin/subscriptions/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSubscription> activateSubscription(@RequestBody SubscriptionActivationRequest request, @AuthenticationPrincipal UserDetailsImpl adminDetails) {
        User admin = new User();
        admin.setId(adminDetails.getId());
        return ResponseEntity.ok(subscriptionService.activateSubscription(request, admin));
    }

    // User-specific APIs
    @PostMapping("/subscriptions/purchase")
    public ResponseEntity<UserSubscriptionDto> purchaseSubscription(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @RequestBody PurchaseRequestDto purchaseRequest) {
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(subscriptionService.purchaseSubscription(user, purchaseRequest.getPlanId(), purchaseRequest.getPaymentMethod()));
    }

    @GetMapping("/subscriptions/my-subscription")
    public ResponseEntity<UserSubscriptionDto> getMySubscription(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(subscriptionService.getMySubscription(user));
    }

    @GetMapping("/subscriptions/history")
    public ResponseEntity<List<UserSubscriptionDto>> getSubscriptionHistory(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(subscriptionService.getSubscriptionHistory(user));
    }
}
