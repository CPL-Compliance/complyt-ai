package integration.mongo_validation;

import com.complyt.SalesTaxApplication;
import integration.TestContainersInitializerIT;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import testUtils.integration_test.ITUtilities;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class SalesTaxTrackingSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document salesTaxTrackingDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        salesTaxTrackingDocument = ITUtilities.salesTaxTrackingDocument();
    }

    @Test
    public void saveSalesTaxTracking_validSalesTaxTracking_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveSalesTaxTracking_missingComplytId_Failure() {
        salesTaxTrackingDocument.remove("complytId");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("complytId"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingTenantId_Failure() {
        salesTaxTrackingDocument.remove("tenantId");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingEnforcesSalesTax_Failure() {
        salesTaxTrackingDocument.remove("enforcesSalesTax");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("enforcesSalesTax"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingPhysicalNexusTracker_Failure() {
        salesTaxTrackingDocument.remove("physicalNexusTracker");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("physicalNexusTracker"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingEconomicNexusTracker_Failure() {
        salesTaxTrackingDocument.remove("economicNexusTracker");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("economicNexusTracker"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingClientTracking_Failure() {
        salesTaxTrackingDocument.remove("clientTracking");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("clientTracking"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingNexusStateRule_Failure() {
        salesTaxTrackingDocument.remove("nexusStateRule");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("nexusStateRule"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingApproved_Failure() {
        salesTaxTrackingDocument.remove("approved");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("approved"))
                .verify();
    }


    @Test
    public void saveSalesTaxTracking_missingApprovalDate_Failure() {
        salesTaxTrackingDocument.remove("approvalDate");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("approvalDate"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_missingAppliedDate_Failure() {
        salesTaxTrackingDocument.remove("appliedDate");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("appliedDate"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_mainSchema_withAdditionalPropertyFalse_Failure() {
        salesTaxTrackingDocument.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_state_withAdditionalPropertyFalse_Failure() {
        ((Document) salesTaxTrackingDocument.get("state")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_physicalNexusTracker_withAdditionalPropertyFalse_Failure() {
        ((Document) salesTaxTrackingDocument.get("physicalNexusTracker")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_economicNexusTracker_withAdditionalPropertyFalse_Failure() {
        ((Document) salesTaxTrackingDocument.get("economicNexusTracker")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_nexusStateRule_withAdditionalPropertyFalse_Failure() {
        ((Document) salesTaxTrackingDocument.get("nexusStateRule")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_nexusStateRule_state_withAdditionalPropertyFalse_Failure() {
        ((Document) ((Document) salesTaxTrackingDocument.get("nexusStateRule")).get("state")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_nexusStateRule_nexusThreshold_withAdditionalPropertyFalse_Failure() {
        ((Document) ((Document) salesTaxTrackingDocument.get("nexusStateRule")).get("nexusThreshold")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_clientTracking_withAdditionalPropertyFalse_Failure() {
        ((Document) salesTaxTrackingDocument.get("clientTracking")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_clientTracking_internalTimestamps_withAdditionalPropertyFalse_Failure() {
        ((Document) ((Document) salesTaxTrackingDocument.get("clientTracking")).get("internalTimestamps")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_clientTracking_nexus_withAdditionalPropertyFalse_Failure() {
        ((Document) ((Document) salesTaxTrackingDocument.get("clientTracking")).get("nexus")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_invalidEconomicNexusTracker_Failure() {
        salesTaxTrackingDocument.put("economicNexusTracker", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("economicNexusTracker"))
                .verify();
    }

    @Test
    public void saveSalesTaxTracking_invalidClientTracking_Failure() {
        salesTaxTrackingDocument.put("clientTracking", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("clientTracking"))
                .verify();
    }
}
