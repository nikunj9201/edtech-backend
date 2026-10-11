package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.TestSeriesSubscriptionActivationRequest;
import com.edtech.edtech_backend.dto.TestSeriesSubscriptionDto;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.TestSeriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestSeriesSubscriptionController {

    private final TestSeriesService testSeriesService;

    public TestSeriesSubscriptionController(TestSeriesService testSeriesService) {
        this.testSeriesService = testSeriesService;
    }

    @PostMapping("/admin/test-series/subscriptions/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestSeriesSubscriptionDto> activateTestSeriesSubscription(
            @RequestBody TestSeriesSubscriptionActivationRequest request,
            @AuthenticationPrincipal UserDetailsImpl adminDetails) {
        User admin = new User();
        admin.setId(adminDetails.getId());
        return ResponseEntity.ok(testSeriesService.activateTestSeriesSubscription(request, admin));
    }

    @GetMapping("/admin/test-series/subscriptions/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TestSeriesSubscriptionDto>> getStudentTestSeriesSubscriptions(@PathVariable Long studentId) {
        return ResponseEntity.ok(testSeriesService.getStudentTestSeriesSubscriptions(studentId));
    }

    @GetMapping("/students/test-series/subscriptions")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<TestSeriesSubscriptionDto>> getMyTestSeriesSubscriptions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = new User();
        user.setId(userDetails.getId());
        return ResponseEntity.ok(testSeriesService.getMyTestSeriesSubscriptions(user));
    }

    @GetMapping("/students/test-series/subscriptions/{studentId}")
    @PreAuthorize("hasRole('ADMIN' , 'STUDENT')")
    public ResponseEntity<List<TestSeriesSubscriptionDto>> getStudentSubscriptionsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(testSeriesService.getStudentTestSeriesSubscriptions(studentId));
    }
}
