package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.ProductClassificationRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class ProductClassificationServiceImpl implements ProductClassificationService {

    @NonNull
    private ProductClassificationRepository productClassificationRepository;

    @Override
    public Mono<Transaction> save(Transaction object) {
        throw new UnsupportedOperationException("save isn't implemented");
    }

    @Override
    public Mono<Transaction> findOneByName(@NonNull String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    @Override
    public Flux<Transaction> findByName(@NonNull String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<Transaction> findById(@NonNull String id) {
        throw new UnsupportedOperationException("findById isn't implemented");
    }

    @Override
    public Flux<Transaction> findAll() {
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
