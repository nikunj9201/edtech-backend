package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.PurchaseRequestDto;
import com.edtech.edtech_backend.dto.SubscriptionAccessResponseDto;
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
import java.util.stream.Collectors;

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

    @GetMapping("/admin/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionAccessResponseDto>> getAllSubscriptions() {
        List<SubscriptionAccessResponseDto> response = subscriptionService.getAllSubscriptions().stream()
                .map(sub -> new SubscriptionAccessResponseDto(
                        sub.getId(),
                        sub.getUser() != null ? sub.getUser().getId() : null,
                        sub.getUser() != null ? sub.getUser().getName() : null,
                        sub.getUser() != null ? sub.getUser().getEmail() : null,
                        sub.getCourse() != null ? sub.getCourse().getId() : null,
                        sub.getCourse() != null ? sub.getCourse().getTitle() : null,
                        sub.getPlan() != null ? sub.getPlan().getName() : null,
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getStatus(),
                        sub.getActivatedByAdmin() != null ? sub.getActivatedByAdmin().getId() : null,
                        sub.getActivatedByAdmin() != null ? sub.getActivatedByAdmin().getName() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/users/{userId}/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionAccessResponseDto>> getSubscriptionsByUser(@PathVariable Long userId) {
        List<SubscriptionAccessResponseDto> response = subscriptionService.getSubscriptionsByUser(userId).stream()
                .map(sub -> new SubscriptionAccessResponseDto(
                        sub.getId(),
                        sub.getUser() != null ? sub.getUser().getId() : null,
                        sub.getUser() != null ? sub.getUser().getName() : null,
                        sub.getUser() != null ? sub.getUser().getEmail() : null,
                        sub.getCourse() != null ? sub.getCourse().getId() : null,
                        sub.getCourse() != null ? sub.getCourse().getTitle() : null,
                        sub.getPlan() != null ? sub.getPlan().getName() : null,
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getStatus(),
                        sub.getActivatedByAdmin() != null ? sub.getActivatedByAdmin().getId() : null,
                        sub.getActivatedByAdmin() != null ? sub.getActivatedByAdmin().getName() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/subscriptions/{subscriptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.ok("Subscription access removed successfully");
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
