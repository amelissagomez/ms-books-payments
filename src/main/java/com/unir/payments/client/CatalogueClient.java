package com.unir.payments.client;


import com.unir.payments.dto.BookValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogueClient {

    private final RestClient restClient;

    public CatalogueClient(@LoadBalanced RestClient.Builder builder,
                           @Value("${catalogue.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
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