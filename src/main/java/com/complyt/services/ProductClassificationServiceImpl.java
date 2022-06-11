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
        throw new UnsupportedOperationException("save isn't implemented");
    }

    @Override
    public Mono<Order> findOneByName(@NonNull String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    @Override
    public Flux<Order> findByName(@NonNull String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<Order> findById(@NonNull String id) {
        throw new UnsupportedOperationException("findById isn't implemented");
    }

    @Override
    public Flux<Order> findAll() {
        throw new UnsupportedOperationException("findAll isn't implemented");
    }

    @Override
    public Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode) {
        return productClassificationRepository.findOneByTaxCode(taxCode);
    }

    public Flux<ProductClassification> getAll() {
        return productClassificationRepository.findAll();
    }

}
