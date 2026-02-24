package com.unir.payments.service;

import com.unir.payments.client.CatalogueClient;
import com.unir.payments.dto.BookValidationResponse;
import com.unir.payments.dto.CreatePurchaseRequest;
import com.unir.payments.dto.PurchaseItemRequest;
import com.unir.payments.dto.PurchaseItemResponse;
import com.unir.payments.dto.PurchaseResponse;
import com.unir.payments.entity.PurchaseEntity;
import com.unir.payments.entity.PurchaseItemEntity;
import com.unir.payments.entity.PurchaseStatus;
import com.unir.payments.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CatalogueClient catalogueClient;

    @Override
    public PurchaseResponse createPurchase(CreatePurchaseRequest request) {

        // Validar items usando API catálogo
        List<PurchaseItemEntity> itemsToSave = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItemRequest itemReq : request.items()) {
            if (itemReq.quantity() <= 0) {
                throw new IllegalArgumentException("Cantidad de libros debe ser mayor a 0=" + itemReq.bookId());
            }

            BookValidationResponse book = catalogueClient.getBook(itemReq.bookId());

            if (!book.visible()) {
                throw new IllegalArgumentException("Libro oculto: " + itemReq.bookId());
            }
            if (book.stock() < itemReq.quantity()) {
                throw new IllegalArgumentException("No hay antidad de libros suficiente para este titulo=" + itemReq.bookId());
            }

            BigDecimal unitPrice = book.price();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            PurchaseItemEntity entityItem = PurchaseItemEntity.builder()
                    .bookId(book.id())
                    .isbn(book.isbn())
                    .title(book.title())
                    .quantity(itemReq.quantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();

            itemsToSave.add(entityItem);
            total = total.add(lineTotal);
        }

        //Calculo de compra 
        PurchaseEntity purchase = PurchaseEntity.builder()
                .customerEmail(request.customerEmail())
                .status(PurchaseStatus.APPROVED)
                .totalAmount(total)
                .createdAt(OffsetDateTime.now())
                .build();

        itemsToSave.forEach(purchase::addItem);

        PurchaseEntity saved = purchaseRepository.save(purchase);

        //Descontar stock en catálogo
        for (PurchaseItemEntity it : saved.getItems()) {
            catalogueClient.decreaseStock(it.getBookId(), it.getQuantity());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchase(Long id) {
        PurchaseEntity purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encuentra compra: " + id));
        return toResponse(purchase);
    }

    private static PurchaseResponse toResponse(PurchaseEntity p) {
        List<PurchaseItemResponse> items = p.getItems().stream()
                .map(i -> new PurchaseItemResponse(
                        i.getBookId(),
                        i.getIsbn(),
                        i.getTitle(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getLineTotal()
                ))
                .toList();

        return new PurchaseResponse(
                p.getId(),
                p.getCustomerEmail(),
                p.getStatus(),
                p.getTotalAmount(),
                p.getCreatedAt(),
                items
        );
    }
}