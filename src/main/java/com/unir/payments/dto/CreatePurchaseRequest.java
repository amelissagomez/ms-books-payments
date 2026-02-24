package com.unir.payments.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreatePurchaseRequest(
        @Email String customerEmail,
        @NotEmpty @Valid List<PurchaseItemRequest> items
) {}