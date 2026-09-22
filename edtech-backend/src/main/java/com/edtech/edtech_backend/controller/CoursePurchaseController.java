package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.CoursePurchaseRequestDto;
import com.edtech.edtech_backend.dto.PurchaseRequestResponseDto;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.CoursePurchaseRequestService;
import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoursePurchaseController {

    private final CoursePurchaseRequestService purchaseService;

    public CoursePurchaseController(CoursePurchaseRequestService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/courses/{courseId}/purchase-request")
    public ResponseEntity<PurchaseRequestResponseDto> createPurchaseRequest(@PathVariable Long courseId,
                                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PurchaseRequestResponseDto resp = purchaseService.createPurchaseRequest(courseId, userDetails.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/admin/course-purchase-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CoursePurchaseRequestDto>> listRequests(@RequestParam(required = false) PurchaseRequestStatus status) {
        return ResponseEntity.ok(purchaseService.listPurchaseRequests(status));
    }

    @PostMapping("/admin/course-purchase-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoursePurchaseRequestDto> approve(@PathVariable Long requestId,
                                                             @AuthenticationPrincipal UserDetailsImpl adminDetails,
                                                             @RequestParam(defaultValue = "365") int durationDays) {
        return ResponseEntity.ok(purchaseService.approveRequest(requestId, adminDetails.getId(), durationDays));
    }

    @PostMapping("/admin/course-purchase-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoursePurchaseRequestDto> reject(@PathVariable Long requestId,
                                                            @AuthenticationPrincipal UserDetailsImpl adminDetails) {
        return ResponseEntity.ok(purchaseService.rejectRequest(requestId, adminDetails.getId()));
    }
}
