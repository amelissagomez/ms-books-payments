package com.unir.payments.service;

import com.unir.payments.dto.CreatePurchaseRequest;
import com.unir.payments.dto.PurchaseResponse;

public interface PurchaseService {
    PurchaseResponse createPurchase(CreatePurchaseRequest request);

    PurchaseResponse getPurchase(Long id);
}