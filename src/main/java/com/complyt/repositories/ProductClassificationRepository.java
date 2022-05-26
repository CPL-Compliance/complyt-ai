package com.complyt.repositories;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
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
//        List<Criteria> criteriaList = new ArrayList<Criteria>(){{
//            add(Criteria.where("taxCode").is("C1S1"));
//            add(Criteria.where("taxCode").is("C2S2"));
//            add(Criteria.where("taxCode").is("C3S3"));
//            add(Criteria.where("taxCode").is("C4S4"));
//        }};
        Query query = Query.query(Criteria.where("taxCode").is("C1S1"));
//        BasicQuery query = new BasicQuery("{ taxCode: { $in: ['C1S1', 'C2S2', 'C3S3', 'C4S4']}}");
        return reactiveMongoTemplate.find(query,ProductClassification.class)
                .map(classification -> {
                    System.out.println("hereeeeee");
                    log.info(classification.toString());
                    return classification;
                });
    }
}
