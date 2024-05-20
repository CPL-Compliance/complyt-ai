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

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class TransactionSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document transactionDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        transactionDocument = ITUtilities.transactionDocument();
    }


    @Test
    public void saveTransaction_validTransaction_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveTransaction_missingTenantId_Failure() {
        transactionDocument.remove("tenantId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingComplytId_Failure() {
        transactionDocument.remove("complytId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("complytId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingCustomerId_Failure() {
        transactionDocument.remove("customerId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingExternalId_Failure() {
        transactionDocument.remove("externalId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingSource_Failure() {
        transactionDocument.remove("source");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("source"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTransactionStatus_Failure() {
        transactionDocument.remove("transactionStatus");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionStatus"))
                .verify();
    }

    @Test
    public void saveTransaction_missingShippingAddress_Failure() {
        transactionDocument.remove("shippingAddress");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("shippingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_missingItems_Failure() {
        transactionDocument.remove("items");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_missingInternalTimestamps_Failure() {
        transactionDocument.remove("internalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("internalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingExternalTimestamps_Failure() {
        transactionDocument.remove("externalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTransactionType_Failure() {
        transactionDocument.remove("transactionType");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionType"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTangibleItemsAmount_Failure() {
        transactionDocument.remove("tangibleItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTaxableItemsAmount_Failure() {
        transactionDocument.remove("taxableItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTotalItemsAmount_Failure() {
        transactionDocument.remove("totalItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTotalDiscount_Failure() {
        transactionDocument.remove("totalDiscount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalDiscount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidIdType_Failure() {
        transactionDocument.put("_id", "invalid_object_id");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_id"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidComplytIdType_Failure() {
        transactionDocument.put("complytId", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("complytId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidCustomerIdType_Failure() {
        transactionDocument.put("customerId", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidExternalIdType_Failure() {
        transactionDocument.put("externalId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidSourceType_Failure() {
        transactionDocument.put("source", 6);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("source"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidDocumentNameType_Failure() {
        transactionDocument.put("documentName", 151);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("documentName"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTransactionStatusType_Failure() {
        transactionDocument.put("transactionStatus", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionStatus"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTenantIdType_Failure() {
        transactionDocument.put("tenantId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidInternalTimestampsType_Failure() {
        transactionDocument.put("internalTimestamps", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("internalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidExternalTimestampsType_Failure() {
        transactionDocument.put("externalTimestamps", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTransactionTypeType_Failure() {
        transactionDocument.put("transactionType", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionType"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTangibleItemsAmountType_Failure() {
        transactionDocument.put("tangibleItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTaxableItemsAmountType_Failure() {
        transactionDocument.put("taxableItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalItemsAmountType_Failure() {
        transactionDocument.put("totalItemsAmount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalDiscountType_Failure() {
        transactionDocument.put("totalDiscount", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalDiscount"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemsType_Failure() {
        transactionDocument.put("items", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidClassType_Failure() {
        transactionDocument.put("_class", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_class"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidSubsidiaryType_Failure() {
        transactionDocument.put("subsidiary", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("subsidiary"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressType_Failure() {
        transactionDocument.put("billingAddress", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("billingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressStreetType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("street", 123); // street should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("street"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressZipType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("zip", 123); // zip should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("zip"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressCountryType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("country", 123); // country should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("country"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressCountyType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("county", 123); // county should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("county"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressStateType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("state", 123); // state should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("state"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidBillingAddressCityType_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("city", 123); // city should be a string

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("city"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidJurisdictionalSalesTaxRulesType_Failure() {
        transactionDocument.put("jurisdictionalSalesTaxRules", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalSalesTaxRules"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidManualSalesTaxType_Failure() {
        transactionDocument.put("manualSalesTax", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("manualSalesTax"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidNameType_Failure() {
        transactionDocument.put("name", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTotalPriceType_Failure() {
        transactionDocument.put("totalPrice", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalPrice"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidUnitPriceType_Failure() {
        transactionDocument.put("unitPrice", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("unitPrice"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTaxCodeType_Failure() {
        transactionDocument.put("taxCode", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemNameType_Failure() {
        Document invalidItem = new Document("name", 123);
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTaxCodeType_Failure() {
        Document invalidItem = new Document("taxCode", 123);
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemManualSalesTaxType_Failure() {
        Document invalidItem = new Document("manualSalesTax", "invalid_object");
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemManualSalesTaxRateType_Failure() {
        Document invalidItem = new Document("manualSalesTaxRate", 123);
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTangibleCategoryType_Failure() {
        Document invalidItem = new Document("tangibleCategory", 123);
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidItemTaxableCategoryType_Failure() {
        Document invalidItem = new Document("taxableCategory", 123);
        transactionDocument.put("items", List.of(invalidItem));
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }
}
