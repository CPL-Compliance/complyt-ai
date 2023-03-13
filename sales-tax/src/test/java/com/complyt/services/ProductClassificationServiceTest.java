package com.complyt.services;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
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
import testUtils.TestUtilities;

import java.time.LocalDateTime;
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

    ProductClassification itemProductClassification0;
    ProductClassification itemProductClassification1;
    ProductClassification shippingFeeProductClassification;
    ObjectId customerId;
    String tenantId;

    Transaction transaction;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        itemProductClassification0 = createItemProductClassification0();
        itemProductClassification1 = createItemProductClassification1();
        shippingFeeProductClassification = createShippingFeeProductClassification();
        customerId = new ObjectId();
        tenantId = UUID.randomUUID().toString();
    }

    private ProductClassification createItemProductClassification0() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C1S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.TANGIBLE);
    }

    private ProductClassification createItemProductClassification1() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C3S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.TANGIBLE);
    }

    private ProductClassification createShippingFeeProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C6S1", "description",
                "title", jurisdictionalSalesTaxRulesList, TangibleCategory.INTANGIBLE);
    }

    private Map<String, JurisdictionalSalesTaxRules> createJurisdictionalSalesTaxRulesList() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();

        return new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = testUtilities.createJurisdictionalSalesTaxRules();

        Item item = transaction.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = testUtilities.createItems(true, true);
        return transaction.withItems(modifiedItems);
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_InjectsDataToTransaction_ReturnsTransaction() {
        // Given
        Transaction givenTransaction = transaction.withShippingFee(null);
        String taxCode0 = givenTransaction.getItems().get(0).getTaxCode();
        String taxCode1 = givenTransaction.getItems().get(1).getTaxCode();
        Transaction transactionWithData = createTransactionWithProductClassificationData()
                .withShippingFee(null)
                .withComplytId(givenTransaction.getComplytId())
                .withExternalId(givenTransaction.getExternalId())
                .withCustomer(givenTransaction.getCustomer());

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode0)).thenReturn(Mono.just(itemProductClassification0));
        when(productClassificationRepository.findOneByTaxCode(taxCode1)).thenReturn(Mono.just(itemProductClassification1));
        Mono<Transaction> actualTransaction = productClassificationService.getTransactionWithRelevantProductClassificationData(givenTransaction);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithData).verifyComplete();
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_InjectsDataToTransactionWithShippingFee_ReturnsTransaction() {
        // Given
        SalesTaxRate salesTaxRate = testUtilities.createSalesTaxRates();
        ShippingFee shippingFee = testUtilities.createShippingFee(true, true).withSalesTaxRate(salesTaxRate);
        Transaction givenTransaction = transaction.withShippingFee(shippingFee);
        String taxCode0 = givenTransaction.getItems().get(0).getTaxCode();
        String taxCode1 = givenTransaction.getItems().get(1).getTaxCode();
        ProductClassification shippingClassification = createShippingFeeProductClassification();
        Transaction transactionWithData = createTransactionWithProductClassificationData()
                .withShippingFee(shippingFee);

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode0)).thenReturn(Mono.just(itemProductClassification0));
        when(productClassificationRepository.findOneByTaxCode(taxCode1)).thenReturn(Mono.just(itemProductClassification1));
        when(productClassificationRepository.findOneByTaxCode(givenTransaction.getShippingFee().getTaxCode())).thenReturn(Mono.just(shippingClassification));
        Mono<Transaction> actualTransaction = productClassificationService.getTransactionWithRelevantProductClassificationData(givenTransaction);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithData).verifyComplete();
    }

    @Test
    void findOneByTaxCode_FindsOne_ReturnsOne() {
        // Given
        String taxCode = itemProductClassification0.getTaxCode();

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(itemProductClassification0));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification0).verifyComplete();
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
        ProductClassification otherProductClassification = itemProductClassification0.withDescription("second classification").withTaxCode("C2S1");
        List<ProductClassification> productClassifications = new ArrayList<ProductClassification>() {{
            add(itemProductClassification0);
            add(otherProductClassification);
        }};

        // When
        when(productClassificationRepository.findAll()).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationService.findAll();

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(itemProductClassification0, otherProductClassification).verifyComplete();
    }

    @Test
    void save_SavesClassification_ReturnsClassification() {
        // Given
        ProductClassification productClassificationNoId = itemProductClassification0.withId(null);

        // When
        when(productClassificationRepository.save(productClassificationNoId)).thenReturn(Mono.just(itemProductClassification0));
        Mono<ProductClassification> productClassificationMono = productClassificationService.save(productClassificationNoId);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification0).verifyComplete();
    }

    @Test
    void findById_FindClassification_ReturnsClassification() {
        // Given
        String id = itemProductClassification0.getId();

        // When
        when(productClassificationRepository.findById(id)).thenReturn(Mono.just(itemProductClassification0));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findById(id);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(itemProductClassification0).verifyComplete();
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