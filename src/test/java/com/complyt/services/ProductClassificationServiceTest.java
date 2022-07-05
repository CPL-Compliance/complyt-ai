package com.complyt.services;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.ProductClassificationRepository;
import org.bson.types.ObjectId;
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

import java.util.*;

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
                "CA",true,false, CalculationType.FIXED,"description",0,null);
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

    @Test
    void setJurisdictionalRules_SetsRules_ReturnsModifiedOrder() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "C1S1",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0
                ));
            }
        };
        Order order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId);
        List<Item> itemsWithRules = new ArrayList<Item>() {{
            add(order.getItems().get(0).withJurisdictionalSalesTaxRules(productClassification.getJurisdictionalSalesTaxRules().get("CA")));
        }};
        Order orderWithItemsWithRules = order.withItems(itemsWithRules);
        OrderJurisdictionalRulesInjector orderProductClassificationInjector = new OrderJurisdictionalRulesInjector(order);

        // When
        when(productClassificationRepository.findOneByTaxCode(order.getItems().get(0).getTaxCode()))
                .thenReturn(Mono.just(productClassification));
        Mono<Order> orderMono = productClassificationService.setJurisdictionalRules(orderProductClassificationInjector);

        // Then
        StepVerifier.create(orderMono).expectNext(orderWithItemsWithRules).verifyComplete();

    }

    @Test
    void save_SaveNotImplemented_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            productClassificationService.save(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "save isn't implemented");
    }

    @Test
    void findOneByName_FindOneByNameNotImplemented_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            productClassificationService.findOneByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findOneByName isn't implemented");
    }

    @Test
    void findByName_findByNameNotImplemented_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            productClassificationService.findByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findByName isn't implemented");
    }

    @Test
    void findById_FindByIdNotImplemented_ThrowsUnsupportedOperationException() {
        // Given
        String id = "id";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            productClassificationService.findById(id);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findById isn't implemented");
    }

    @Test
    void findAll_FindAllIdNotImplemented_ThrowsUnsupportedOperationException() {
        // Given
        String id = "id";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            productClassificationService.findAll();
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findAll isn't implemented");
    }


}
