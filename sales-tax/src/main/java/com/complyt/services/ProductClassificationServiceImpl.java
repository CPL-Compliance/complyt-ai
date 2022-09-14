package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.utils.data_injector.TransactionProductClassificationDataInjector;
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


    public Mono<ProductClassification> save(ProductClassification productClassification) {
        return productClassificationRepository.save(productClassification);
    }

    @Override
    public Mono<ProductClassification> findById(@NonNull String id) {
        return productClassificationRepository.findById(id);
    }

    @Override
    public Flux<ProductClassification> findAll() {
        return productClassificationRepository.findAll();
    }

    @Override
    public Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode) {
        return productClassificationRepository.findOneByTaxCode(taxCode);
    }

    @Override
    public Mono<Transaction> getTransactionWithRelevantProductClassificationData(Transaction transaction) {
        return Flux.fromIterable(transaction.getItems())
                .flatMap(item -> getClassification(item.getTaxCode()))
                .collectMap(ProductClassification::getTaxCode, productClassification -> productClassification)
                .flatMap(mapTaxCodesToClassifications -> new TransactionProductClassificationDataInjector(transaction).inject(mapTaxCodesToClassifications));
    }

    private Mono<ProductClassification> getClassification(String taxCode) {
        return findOneByTaxCode(taxCode);
    }
}
