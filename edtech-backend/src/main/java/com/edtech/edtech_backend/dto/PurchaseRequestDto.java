package com.edtech.edtech_backend.dto;

import lombok.Data;

@Data
public class PurchaseRequestDto {
    private Long planId;
    private String paymentMethod;
}
