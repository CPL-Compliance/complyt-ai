package com.complyt.services;

import com.complyt.business.transaction.data_injector.TransactionProductClassificationDataInjector;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.ProductClassificationRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductClassificationServiceTest {

    @InjectMocks
    ProductClassificationServiceImpl productClassificationService;

    @Mock
    ProductClassificationRepository productClassificationRepository;

    @Mock
    TransactionProductClassificationDataInjector transactionProductClassificationDataInjector;

    ProductClassification firstItemProductClassification;
    ProductClassification secondItemProductClassification;
    ProductClassification shippingFeeProductClassification;
    ObjectId customerId;
    String tenantId;

    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        firstItemProductClassification = createFirstItemProductClassification();
        secondItemProductClassification = createSecondItemProductClassification();
        shippingFeeProductClassification = createShippingFeeProductClassification();
        customerId = new ObjectId();
        tenantId = UUID.randomUUID().toString();
    }

    private ProductClassification createFirstItemProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C1S1", "description",
                "title", jurisdictionalSalesTaxRulesList, null, TangibleCategory.TANGIBLE);// todo: gst is null - we should check gst here
    }

    private ProductClassification createSecondItemProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C3S1", "description",
                "title", jurisdictionalSalesTaxRulesList, null, TangibleCategory.TANGIBLE);
    }

    private ProductClassification createShippingFeeProductClassification() {
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = createJurisdictionalSalesTaxRulesList();
        return new ProductClassification("id", "C6S1", "description",
                "title", jurisdictionalSalesTaxRulesList, null, TangibleCategory.INTANGIBLE);
    }

    private Map<String, JurisdictionalSalesTaxRules> createJurisdictionalSalesTaxRulesList() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();

        return new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
    }

    private Transaction createTransactionWithProductClassificationData() {
        List<Item> modifiedItems = testUtilities.createItems(true, false, true);
        return transaction.withItems(modifiedItems);
    }

//    @Test
//    void getTransactionWithRelevantProductClassificationData_InjectsDataToTransaction_ReturnsTransaction() {
//        // Given
//        Transaction givenTransaction = transaction.withShippingFee(null);
//        String taxCode0 = givenTransaction.getItems().get(0).getTaxCode();
//        String taxCode1 = givenTransaction.getItems().get(1).getTaxCode();
//
//        JurisdictionalSalesTaxRules firstRule = new JurisdictionalSalesTaxRules("rule1", "CA", true, false,
//                CalculationType.FIXED, "rule1", BigDecimal.ZERO, null);
//        JurisdictionalSalesTaxRules secondRule = new JurisdictionalSalesTaxRules("rule2", "CA", true, false,
//                CalculationType.FIXED, "rule2", BigDecimal.ZERO, null);
//
//        Transaction transactionWithData = createTransactionWithProductClassificationData()
//                .withShippingFee(null)
//                .withComplytId(givenTransaction.getComplytId())
//                .withExternalId(givenTransaction.getExternalId())
//                .withCustomer(givenTransaction.getCustomer());
//
//        Map<String, ProductClassification> mapTaxCodesToClassifications = testUtilities.createUsaClassificationsMap(firstRule, secondRule);
//
//        // When
//        when(productClassificationRepository.findOneByTaxCode(taxCode0)).thenReturn(Mono.just(mapTaxCodesToClassifications.get(taxCode0)));
//        when(productClassificationRepository.findOneByTaxCode(taxCode1)).thenReturn(Mono.just(mapTaxCodesToClassifications.get(taxCode1)));
//        when(transactionProductClassificationDataInjector.inject(mapTaxCodesToClassifications, givenTransaction)).thenReturn(Mono.just(transactionWithData));
//
//        Mono<Transaction> actualTransaction = productClassificationService.getTransactionWithRelevantProductClassificationData(givenTransaction);
//
//        // Then
//        StepVerifier.create(actualTransaction).expectNext(transactionWithData).verifyComplete();
//    }

    @Test
    void getTransactionWithRelevantProductClassificationData_WithValidTransactionWithShippingFee_ShouldInjectClassificationCorrectly() {
        // Prepare test data
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        ShippingFee shippingFee = testUtilities.createShippingFee(true, false, true).withSalesTaxRates(salesTaxRates);
        Transaction givenTransaction = transaction.withShippingFee(shippingFee);
        String taxCode0 = givenTransaction.getItems().get(0).getTaxCode();
        String taxCode1 = givenTransaction.getItems().get(1).getTaxCode();
        ProductClassification shippingClassification = createShippingFeeProductClassification();

        Transaction transactionWithFullData = createTransactionWithProductClassificationData()
                .withShippingFee(shippingFee);
        Map<String, ProductClassification> mapTaxCodesToClassifications = testUtilities
                .createUsaClassificationsMap(givenTransaction.getItems().get(0).getJurisdictionalSalesTaxRules(), givenTransaction.getItems().get(1).getJurisdictionalSalesTaxRules());
        mapTaxCodesToClassifications.put(shippingFee.getTaxCode(), shippingClassification);

        // Mocking the repository call to return a classification for any tax code
        when(productClassificationRepository.findOneByTaxCode(taxCode0)).thenReturn(Mono.just(firstItemProductClassification));
        when(productClassificationRepository.findOneByTaxCode(taxCode1)).thenReturn(Mono.just(secondItemProductClassification));
        when(productClassificationRepository.findOneByTaxCode(givenTransaction.getShippingFee().getTaxCode())).thenReturn(Mono.just(shippingClassification));

        // Mocking the injector to return the transaction itself for simplicity
        when(transactionProductClassificationDataInjector.inject(mapTaxCodesToClassifications, givenTransaction))
                .thenReturn(Mono.just(transactionWithFullData));

        // Executing the method under test
        Mono<Transaction> result = productClassificationService.getTransactionWithRelevantProductClassificationData(givenTransaction);

        // Assertions
        StepVerifier.create(result).expectNext(transactionWithFullData).verifyComplete();
    }

    @Test
    void getTransactionWithRelevantProductClassificationData_WithValidTransaction_ShouldInjectClassificationCorrectlyy() {
        // Given
        Transaction givenTransaction = transaction.withShippingFee(null);
        String taxCode0 = "C1S1";
        String taxCode1 = "C3S1";

        Transaction transactionWithFullData = createTransactionWithProductClassificationData().withShippingFee(null);

        Map<String, ProductClassification> mapTaxCodesToClassifications = testUtilities
                .createUsaClassificationsMap(
                        givenTransaction.getItems().get(0).getJurisdictionalSalesTaxRules(),
                        givenTransaction.getItems().get(1).getJurisdictionalSalesTaxRules()
                );

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode0)).thenReturn(Mono.just(firstItemProductClassification));
        when(productClassificationRepository.findOneByTaxCode(taxCode1)).thenReturn(Mono.just(secondItemProductClassification));

        when(transactionProductClassificationDataInjector.inject(mapTaxCodesToClassifications, givenTransaction))
                .thenReturn(Mono.just(transactionWithFullData));

        // Then
        Mono<Transaction> result = productClassificationService.getTransactionWithRelevantProductClassificationData(givenTransaction);

        StepVerifier.create(result).expectNext(transactionWithFullData).verifyComplete();
    }

    @Test
    void findOneByTaxCode_FindsOne_ReturnsOne() {
        // Given
        String taxCode = firstItemProductClassification.getTaxCode();

        // When
        when(productClassificationRepository.findOneByTaxCode(taxCode)).thenReturn(Mono.just(firstItemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findOneByTaxCode(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(firstItemProductClassification).verifyComplete();
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
        ProductClassification otherProductClassification = firstItemProductClassification.withDescription("second classification").withTaxCode("C2S1");
        List<ProductClassification> productClassifications = new ArrayList<>() {{
            add(firstItemProductClassification);
            add(otherProductClassification);
        }};

        // When
        when(productClassificationRepository.findAll()).thenReturn(Flux.fromIterable(productClassifications));
        Flux<ProductClassification> productClassificationFlux = productClassificationService.findAll(0, productClassifications.size());

        // Then
        StepVerifier.create(productClassificationFlux).expectNext(firstItemProductClassification, otherProductClassification).verifyComplete();
    }

    @Test
    void save_SavesClassification_ReturnsClassification() {
        // Given
        ProductClassification productClassificationNoId = firstItemProductClassification.withId(null);

        // When
        when(productClassificationRepository.save(productClassificationNoId)).thenReturn(Mono.just(firstItemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.save(productClassificationNoId);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(firstItemProductClassification).verifyComplete();
    }

    @Test
    void findById_FindClassification_ReturnsClassification() {
        // Given
        String id = firstItemProductClassification.getId();

        // When
        when(productClassificationRepository.findById(id)).thenReturn(Mono.just(firstItemProductClassification));
        Mono<ProductClassification> productClassificationMono = productClassificationService.findById(id);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(firstItemProductClassification).verifyComplete();
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