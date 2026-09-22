package com.edtech.edtech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestResponseDto {
    private Long requestId;
    private String whatsappUrl;
}
