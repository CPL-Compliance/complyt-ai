package com.complyt.services;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.OrderRepository;
import com.complyt.repositories.ProductClassificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductClassificationServiceTest {
    @InjectMocks
    ProductClassificationServiceImpl productClassificationService;

    @Mock
    ProductClassificationRepository productClassificationRepository;

    ProductClassification productClassification;

    @BeforeEach
    void setUp(){
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA",true,false, CalculationType.FIXED,"description",0);
        Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = new HashMap<String,JurisdictionalSalesTaxRules>(){{
            put(jurisdictionalSalesTaxRules.getAbbreviation(),jurisdictionalSalesTaxRules);
        }};
        productClassification = new ProductClassification("id","C1S1","description",
                "title",jurisdictionalSalesTaxRulesList);
    }

    @Test
    void findOneByTaxCode_FindsOne_ReturnsOne(){
        // Given
        String taxCode = productClassification.getTaxCode();

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();
    }

    @Test
    void findOneByTaxCode_NullTaxCodeGiven_ThrowsException(){
        // Given
        String taxCode = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            productClassificationService.findOneByTaxCode(taxCode);
        });

        assertEquals(nullPointerException.getMessage(), "taxCode is marked non-null but is null");
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
        when(productClassificationRepository.findAll()).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationService.getAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(productClassification,otherProductClassification).verifyComplete();
    }

}
