package integration.mongo_validation;

import com.complyt.SalesTaxApplication;
import integration.TestContainersInitializerIT;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import testUtils.integration_test.ITUtilities;

import javax.print.Doc;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class TransactionSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document TransactionDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        TransactionDocument = ITUtilities.buildDocument();
    }


    @Test
    public void saveTransaction_validTransaction_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveTransaction_missingTenantId_Failure() {
        TransactionDocument.remove("tenantId");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingComplytId_Failure() {
        TransactionDocument.remove("complytId");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("complytId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingCustomerId_Failure() {
        TransactionDocument.remove("customerId");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingExternalId_Failure() {
        TransactionDocument.remove("externalId");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingSource_Failure() {
        TransactionDocument.remove("source");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("source"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTransactionStatus_Failure() {
        TransactionDocument.remove("transactionStatus");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionStatus"))
                .verify();
    }

    @Test
    public void saveTransaction_missingShippingAddress_Failure() {
        TransactionDocument.remove("shippingAddress");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("shippingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_missingItems_Failure() {
        TransactionDocument.remove("items");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_missingInternalTimestamps_Failure() {
        TransactionDocument.remove("internalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("internalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingExternalTimestamps_Failure() {
        TransactionDocument.remove("externalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTransactionType_Failure() {
        TransactionDocument.remove("transactionType");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionType"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTangibleItemsAmount_Failure() {
        TransactionDocument.remove("tangibleItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTaxableItemsAmount_Failure() {
        TransactionDocument.remove("taxableItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTotalItemsAmount_Failure() {
        TransactionDocument.remove("totalItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTotalDiscount_Failure() {
        TransactionDocument.remove("totalDiscount");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalDiscount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidIdType_Failure() {
        TransactionDocument.put("_id", "invalid_object_id");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_id"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidComplytIdType_Failure() {
        TransactionDocument.put("complytId", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("complytId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidCustomerIdType_Failure() {
        TransactionDocument.put("customerId", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidExternalIdType_Failure() {
        TransactionDocument.put("externalId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidSourceType_Failure() {
        TransactionDocument.put("source", 6);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("source"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidDocumentNameType_Failure() {
        TransactionDocument.put("documentName", 151);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("documentName"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTransactionStatusType_Failure() {
        TransactionDocument.put("transactionStatus", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionStatus"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTenantIdType_Failure() {
        TransactionDocument.put("tenantId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidInternalTimestampsType_Failure() {
        TransactionDocument.put("internalTimestamps", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("internalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidExternalTimestampsType_Failure() {
        TransactionDocument.put("externalTimestamps", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTransactionTypeType_Failure() {
        TransactionDocument.put("transactionType", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionType"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTangibleItemsAmountType_Failure() {
        TransactionDocument.put("tangibleItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTaxableItemsAmountType_Failure() {
        TransactionDocument.put("taxableItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalItemsAmountType_Failure() {
        TransactionDocument.put("totalItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalDiscountType_Failure() {
        TransactionDocument.put("totalDiscount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalDiscount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemsType_Failure() {
        TransactionDocument.put("items", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidClassType_Failure() {
        TransactionDocument.put("_class", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_class"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidSubsidiaryType_Failure() {
        TransactionDocument.put("subsidiary", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("subsidiary"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressType_Failure() {
        TransactionDocument.put("billingAddress", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("billingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidJurisdictionalSalesTaxRulesType_Failure() {
        TransactionDocument.put("jurisdictionalSalesTaxRules", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalSalesTaxRules"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidManualSalesTaxType_Failure() {
        TransactionDocument.put("manualSalesTax", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("manualSalesTax"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidNameType_Failure() {
        TransactionDocument.put("name", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalPriceType_Failure() {
        TransactionDocument.put("totalPrice", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalPrice"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidUnitPriceType_Failure() {
        TransactionDocument.put("unitPrice", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("unitPrice"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTaxCodeType_Failure() {
        TransactionDocument.put("taxCode", 123);
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemNameType_Failure() {
        Document invalidItem = new Document("name", 123);
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTaxCodeType_Failure() {
        Document invalidItem = new Document("taxCode", 123);
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemManualSalesTaxType_Failure() {
        Document invalidItem = new Document("manualSalesTax", "invalid_object");
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemManualSalesTaxRateType_Failure() {
        Document invalidItem = new Document("manualSalesTaxRate", 123);
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTangibleCategoryType_Failure() {
        Document invalidItem = new Document("tangibleCategory", 123);
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTaxableCategoryType_Failure() {
        Document invalidItem = new Document("taxableCategory", 123);
        TransactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(TransactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }
}
