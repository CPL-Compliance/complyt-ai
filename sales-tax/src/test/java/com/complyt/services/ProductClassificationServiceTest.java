package com.complyt.services;

import com.complyt.domain.*;
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

    ProductClassification itemProductClassification;
    ProductClassification shippingFeeProductClassification;
    ObjectId customerId;
    String tenantId;

    @BeforeEach
    void setUp() {
        itemProductClassification = createItemProductClassification();
        shippingFeeProductClassification = createShippingFeeProductClassification();
        customerId = new ObjectId();
        tenantId = UUID.randomUUID().toString();
    }

    private ProductClassification createItemProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C1S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.TANGIBLE);
    }

    private ProductClassification createShippingFeeProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C6S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.INTANGIBLE);
    }

    private Map<String, JurisdictionalSalesTaxRules> createJurisdictionalSalesTaxRulesList() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA", true, false, CalculationType.FIXED, "description", 0, null);

        return new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
    }

    private Transaction createTransaction() {
        String id = null;
        String externalId = "externalId";
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, null, TransactionType.INVOICE, null);
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        Transaction transaction = createTransaction();

        Item item = transaction.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{
            add(item);
        }};
        return transaction.withItems(modifiedItems);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_InjectsDataToTransaction_ReturnsTransaction() {
        // Given
        Transaction transaction = createTransaction();
        String itemTaxCode = transaction.getItems().get(0).getTaxCode();
        Transaction transactionWithData = createTransactionWithProductClassificationData();

        // When
        when(productClassificationRepository.findOneByTaxCode(itemTaxCode)).thenReturn(Mono.just(itemProductClassification));
        Mono<Transaction> actualtransaction = productClassificationService.getTransactionWithRelevantProductClassificationData(transaction);

        // Then
        StepVerifier.create(actualtransaction).expectNext(transactionWithData).verifyComplete();
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_InjectsDataToTransactionWithShippingFee_ReturnsTransaction() {
        // Given
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        ShippingFee shippingFee = new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules, salesTaxRate, "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
        Transaction transaction = createTransaction().withShippingFee(shippingFee);
        String taxCode = transaction.getItems().get(0).getTaxCode();
        ProductClassification shippingClassification = createShippingFeeProductClassification();
        Transaction transactionWithData = createTransactionWithProductClassificationData().withShippingFee(shippingFee);

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(itemProductClassification));
        when(productClassificationRepository.findOneByTaxCode(transaction.getShippingFee().getTaxCode())).thenReturn(Mono.just(shippingClassification));
        Mono<Transaction> actualTransaction = productClassificationService.getTransactionWithRelevantProductClassificationData(transaction);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithData).verifyComplete();
    }

//    @Test
//    void getShippingFeeClassification_FindsClassification_ReturnsClassification() {
//        // Given
//        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
//        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
//        ShippingFee shippingFee = new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules, salesTaxRate, "C6S1");
//        ProductClassification shippingClassification = createShippingFeeProductClassification();
//        Transaction transaction = createTransaction().withShippingFee(shippingFee);
//
//        // When
//        when(productClassificationRepository.findOneByTaxCode(transaction.getShippingFee().getTaxCode())).thenReturn(Mono.just(shippingClassification));
//        Mono<ProductClassification> productClassificationMono = productClassificationService.g
//
//        // Then
//    }


    @Test
    void findOneByTaxCode_FindsOne_ReturnsOne() {
        // Given
        String taxCode = itemProductClassification.getTaxCode();

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(itemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification).verifyComplete();
    }

    @Test
    void findOneByTaxCode_NullTaxCodeGiven_ThrowsException() {
        // Given
        String taxCode = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            productClassificationService.findOneByTaxCode(taxCode);
        });

        assertEquals(nullPointerException.getMessage(), "taxCode is marked non-null but is null");
    }

    @Test
    void findAll_FindsAllClassifications_ReturnsAllClassifications() {
        // Given
        ProductClassification otherProductClassification = itemProductClassification.withDescription("second classification").withTaxCode("C2S1");
        List<ProductClassification> productClassifications = new ArrayList<ProductClassification>() {{
            add(itemProductClassification);
            add(otherProductClassification);
        }};

        // When
        when(productClassificationRepository.findAll()).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationService.findAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(itemProductClassification, otherProductClassification).verifyComplete();
    }

    @Test
    void save_SavesClassification_ReturnsClassification() {
        // Given
        ProductClassification productClassificationNoId = itemProductClassification.withId(null);

        // When
        when(productClassificationRepository.save(productClassificationNoId)).thenReturn(Mono.just(itemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.save(productClassificationNoId);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification).verifyComplete();
    }

    @Test
    void findById_FindClassification_ReturnsClassification() {
        // Given
        String id = itemProductClassification.getId();

        // When
        when(productClassificationRepository.findById(id)).thenReturn(Mono.just(itemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findById(id);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            productClassificationService.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            productClassificationService.getTransactionWithRelevantProductClassificationData(nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}
