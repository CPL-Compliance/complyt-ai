package com.complyt.services;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.CustomerType;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
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
    ObjectId customerId;
    ObjectId clientId;

    @BeforeEach
    void setUp(){
        productClassification = createProductClassification();
        customerId = new ObjectId();
        clientId = new ObjectId();
    }

    private ProductClassification createProductClassification() {
        Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id","C1S1","description",
                "title",jurisdictionalSalesTaxRulesList,TangibleCategory.TANGIBLE);
    }

    private Map<String, JurisdictionalSalesTaxRules> createJurisdictionalSalesTaxRulesList() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA",true,false, CalculationType.FIXED,"description",0,null);

        return new HashMap<String,JurisdictionalSalesTaxRules>(){{
            put(jurisdictionalSalesTaxRules.getAbbreviation(),jurisdictionalSalesTaxRules);
        }};
    }

    private Order createOrder() {
        String id = null;
        String externalId = "externalId";
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        return new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId, null, null);
    }

    private Order createOrderWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        Order order = createOrder();

        Item item = order.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{add(item);}};
        return order.withItems(modifiedItems);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California","CA",true,
                false,CalculationType.FIXED,"description",0,null);
    }

    @Test
    void getOrderWithRelevantProductClassificationData_InjectsDateToOrder_ReturnsOrder() {
        // Given
        Order order = createOrder();
        String taxCode = order.getItems().get(0).getTaxCode();
        Order orderWithData = createOrderWithProductClassificationData();

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));
        Mono<Order> actualOrder = productClassificationService.getOrderWithRelevantProductClassificationData(order);

        // Then
        StepVerifier.create(actualOrder).expectNext(orderWithData).verifyComplete();
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
        Flux<ProductClassification> productClassificationFlux = productClassificationService.findAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(productClassification,otherProductClassification).verifyComplete();
    }

    @Test
    void save_SavesClassification_ReturnsClassification(){
        // Given
        ProductClassification productClassificationNoId = productClassification.withId(null);

        // When
        when(productClassificationRepository.save(productClassificationNoId)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.save(productClassificationNoId);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();
    }

    @Test
    void findById_FindClassification_ReturnsClassification(){
        // Given
        String id = productClassification.getId();

        // When
        when(productClassificationRepository.findById(id)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findById(id);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();
    }


    @Test
    void findById_NullIdPassed_ThrowsException(){
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            productClassificationService.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

}
