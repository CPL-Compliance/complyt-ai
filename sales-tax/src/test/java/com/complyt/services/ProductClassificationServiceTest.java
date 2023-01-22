package com.complyt.services;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.timestamps.ComplytTimestamp;
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
import testUtils.DomainObjectStub;

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
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
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
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = domainObjectStub.createJurisdictionalSalesTaxRules();

        return new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = domainObjectStub.createJurisdictionalSalesTaxRules();

        Item item = transaction.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = domainObjectStub.createItems(true, true);
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
        SalesTaxRate salesTaxRate = domainObjectStub.createSalesTaxRates();
        ShippingFee shippingFee = domainObjectStub.createShippingFee(true, true).withSalesTaxRate(salesTaxRate);
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

// Transaction(complytId=3e4b6a0d-d656-4c30-9adc-a0ec34cd4398, id=5144226a-17e8-4f14-b6ef-f3b6f733566f, externalId=5144226a-17e8-4f14-b6ef-f3b6f733566f, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C3S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc302a91fc5e3d78fb9e7e, customer=Customer(complytId=f106bf13-5c29-4068-97ba-af791f0d410c, id=63bc302a91fc5e3d78fb9e7e, externalId=63bc302a91fc5e3d78fb9e7e, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=c97de989-888a-4e3c-af2f-82c74c441e6b, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826), updatedDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T17:17:02.779826), updatedDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826))), salesTax=null, transactionStatus=ACTIVE, tenantId=c97de989-888a-4e3c-af2f-82c74c441e6b, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826), updatedDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826), updatedDate=ComplytTimestamp(timestamp=2023-01-09T17:18:02.779826)), transactionType=INVOICE, shippingFee=null, createdFrom=null))
// Transaction(complytId=6e5be361-e471-4a8d-992d-7f5475b90d8a, id=50e4f104-61eb-4e2b-97e6-96cb05cb3b91, externalId=50e4f104-61eb-4e2b-97e6-96cb05cb3b91, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc2505aaf5026f61b6c2f7, customer=Customer(complytId=c6842a2a-15da-42e8-ae29-79e9eafa0cbf, id=63bc2505aaf5026f61b6c2f7, externalId=63bc2505aaf5026f61b6c2f7, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=13b5773d-6472-405e-ab58-ce485edc1646, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896), updatedDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T16:29:29.239896), updatedDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896))), salesTax=null, transactionStatus=ACTIVE, tenantId=13b5773d-6472-405e-ab58-ce485edc1646, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896), updatedDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896), updatedDate=ComplytTimestamp(timestamp=2023-01-09T16:30:29.239896)), transactionType=INVOICE, shippingFee=null, createdFrom=null))