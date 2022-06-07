package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.ProductClassificationRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ProductClassificationServiceImpl implements ProductClassificationService {

    @NonNull
    private ProductClassificationRepository productClassificationRepository;

    @Override
    public Mono<Order> save(Order object) {
        return null;
    }

    @Override
    public Mono<Order> findOneByName(@NonNull String name) {
        return null;
    }

    @Override
    public Flux<Order> findByName(@NonNull String name) {
        return null;
    }

    @Override
    public Mono<Order> findById(@NonNull String id) {
        return null;
    }

    @Override
    public Flux<Order> findAll() {
        return null;
    }

    @Override
    public Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode) {
        return productClassificationRepository.findOneByTaxCode(taxCode);
    }

    public Flux<ProductClassification> getAll() {
        return productClassificationRepository.findAll();
    }

}
