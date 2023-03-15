package com.complyt.repositories;

import com.complyt.domain.nexus.enums.TangibleCategory;
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
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ProductClassificationRepositoryTest {
    @InjectMocks
    ProductClassificationRepository productClassificationRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    ProductClassification productClassification;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};

        productClassification = new ProductClassification(UUID.randomUUID().toString(), "C1S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.TANGIBLE);
    }

    @Test
    void findOneByTaxCode_FindsClassification_ReturnsClassification() {
        // Given
        String taxCode = productClassification.getTaxCode();
        Query query = Query.query(Criteria.where("taxCode").is(taxCode));

        // When
        when(reactiveMongoTemplate.findOne(query, ProductClassification.class)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationRepository.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();
    }

    @Test
    void findAll_FindsAllClassifications_ReturnsAllClassifications() {
        // Given
        ProductClassification otherProductClassification = productClassification.withDescription("second classification").withTaxCode("C2S1");
        List<ProductClassification> productClassifications = new ArrayList<>() {{
            add(productClassification);
            add(otherProductClassification);
        }};

        // When
        when(reactiveMongoTemplate.findAll(ProductClassification.class)).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationRepository.findAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(productClassification, otherProductClassification).verifyComplete();
    }

    @Test
    void findById_FindsClassification_ReturnsClassification() {
        // Given
        String id = productClassification.getId();
        Query query = Query.query(Criteria.where("_id").is(id));

        // When
        when(reactiveMongoTemplate.findOne(query, ProductClassification.class)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> actualClassification = productClassificationRepository.findById(id);

        // Then
        StepVerifier.create(actualClassification).expectNext(productClassification).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> productClassificationRepository.findById(nullId));

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void save_SavesClassification_ReturnsClassification() {
        // Given
        ProductClassification productClassificationNoId = productClassification.withId(null);

        // When
        when(reactiveMongoTemplate.save(productClassificationNoId)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> actualClassification = productClassificationRepository.save(productClassificationNoId);

        // Then
        StepVerifier.create(actualClassification).expectNext(productClassification).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void save_NullClassificationPassed_ThrowsException() {
        // Given
        ProductClassification nullClassification = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> productClassificationRepository.save(nullClassification));

        assertEquals(nullPointerException.getMessage(), "productClassification is marked non-null but is null");
    }

}
