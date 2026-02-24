package com.unir.payments.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseItemRequest(
        @NotNull Long bookId,
        @Min(1) int quantity
) {}