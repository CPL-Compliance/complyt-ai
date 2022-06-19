package com.complyt.repositories;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ProductClassificationRepositoryTest {
    @InjectMocks
    ProductClassificationRepository productClassificationRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    ProductClassification productClassification;

    @BeforeEach
    void setUp(){
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA",true,false, CalculationType.FIXED,"description",0,null);
        Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = new HashMap<String,JurisdictionalSalesTaxRules>(){{
            put(jurisdictionalSalesTaxRules.getAbbreviation(),jurisdictionalSalesTaxRules);
        }};
        productClassification = new ProductClassification("id","C1S1","description",
                "title",jurisdictionalSalesTaxRulesList);
    }

    @Test
    void findOneByTaxCode_FindsClassification_ReturnsClassification(){
        // Given
        String taxCode = productClassification.getTaxCode();
        Query query = Query.query(Criteria.where("taxCode").is(taxCode));

        // When
        when(reactiveMongoTemplate.findOne(query,ProductClassification.class)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationRepository.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();
    }

    @Test
    void findAll_FindsAllClassifications_ReturnsAllClassifications(){
        // Given
        ProductClassification otherProductClassification = productClassification.withDescription("second classification").withTaxCode("C2S1");
        List<ProductClassification> productClassifications =  new ArrayList<ProductClassification>(){{
            add(productClassification);
            add(otherProductClassification);
        }};

        // When
        when(reactiveMongoTemplate.findAll(ProductClassification.class)).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationRepository.findAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(productClassification,otherProductClassification).verifyComplete();
    }
}
