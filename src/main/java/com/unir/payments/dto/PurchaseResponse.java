package com.unir.payments.dto;

import com.unir.payments.entity.PurchaseStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PurchaseResponse(
        Long id,
        String customerEmail,
        PurchaseStatus status,
        BigDecimal totalAmount,
        OffsetDateTime createdAt,
        List<PurchaseItemResponse> items
) {}