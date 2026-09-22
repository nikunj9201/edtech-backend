package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.CoursePurchaseRequestDto;
import com.edtech.edtech_backend.dto.PurchaseRequestResponseDto;
import com.edtech.edtech_backend.entity.CoursePurchaseRequest;
import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoursePurchaseRequestService {
    PurchaseRequestResponseDto createPurchaseRequest(Long courseId, Long userId);
    List<CoursePurchaseRequestDto> listPurchaseRequests(PurchaseRequestStatus status);
    CoursePurchaseRequestDto approveRequest(Long requestId, Long adminUserId, int durationDays);
    CoursePurchaseRequestDto rejectRequest(Long requestId, Long adminUserId);
}
