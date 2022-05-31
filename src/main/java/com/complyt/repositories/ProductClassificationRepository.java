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

import java.util.Set;

@Repository
@Slf4j
@AllArgsConstructor
public class ProductClassificationRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ProductClassification> findOneByTaxCode(String taxCode) {
        Query query = Query.query(Criteria.where("taxCode").is(taxCode));

        return reactiveMongoTemplate.findOne(query, ProductClassification.class);
    }

    public Flux<ProductClassification> findByTaxCodes(Set<String> taxCodes) {
        return null;
    }

    public Flux<ProductClassification> findAll() {
        return reactiveMongoTemplate.findAll(ProductClassification.class);
    }
}
