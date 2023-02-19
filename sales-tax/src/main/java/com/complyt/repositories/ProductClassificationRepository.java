package com.complyt.repositories;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
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
        log.debug("Searching for product classification for tax code : " + taxCode);

        return reactiveMongoTemplate.findOne(query, ProductClassification.class);
    }

    public Flux<ProductClassification> findAll() {
        return reactiveMongoTemplate.findAll(ProductClassification.class);
    }

    public Mono<ProductClassification> findById(@NonNull String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        log.debug("Searching for a productClassification with id of : " + id);

        return reactiveMongoTemplate.findOne(query, ProductClassification.class);
    }

    public Mono<ProductClassification> save(@NonNull ProductClassification productClassification) {
        return reactiveMongoTemplate.save(productClassification);
    }
}
