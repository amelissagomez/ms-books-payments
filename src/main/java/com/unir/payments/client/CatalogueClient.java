package com.unir.payments.client;


import com.unir.payments.dto.BookValidationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogueClient {

    private final RestClient restClient;

    public CatalogueClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8081/api/v1/internal/books")
                .build();
    }

    public BookValidationResponse getBook(Long bookId) {
        return restClient.get()
                .uri("/{id}", bookId)
                .retrieve()
                .body(BookValidationResponse.class);
    }

    public void decreaseStock(Long bookId, int qty) {
        restClient.post()
                .uri("/{id}/decrease-stock?qty={q}", bookId, qty)
                .retrieve()
                .toBodilessEntity();
    }
}