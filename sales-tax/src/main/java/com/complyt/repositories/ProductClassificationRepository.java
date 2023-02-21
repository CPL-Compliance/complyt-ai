package com.complyt.repositories;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@AllArgsConstructor
public class ProductClassificationRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ProductClassification> findOneByTaxCode(String taxCode) {
        Query query = Query.query(Criteria.where("taxCode").is(taxCode));

        return ContextLogger.observeCtx("Searching for product classification with tax code " + taxCode, log::info)
                .then(reactiveMongoTemplate.findOne(query, ProductClassification.class));
    }

    public Flux<ProductClassification> findAll() {
        return reactiveMongoTemplate.findAll(ProductClassification.class);
    }

    public Mono<ProductClassification> findById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));

        return ContextLogger.observeCtx("Searching for a product classification with ID " + id, log::info)
                .then(reactiveMongoTemplate.findOne(query, ProductClassification.class));
    }

    public Mono<ProductClassification> save(@NonNull ProductClassification productClassification) {
        return ContextLogger.observeCtx("Saving product classification " + productClassification, log::info)
                .then(reactiveMongoTemplate.save(productClassification));
    }
}