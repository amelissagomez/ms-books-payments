package com.unir.payments.dto;

import java.math.BigDecimal;

public record PurchaseItemResponse(
        Long bookId,
        String isbn,
        String title,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}