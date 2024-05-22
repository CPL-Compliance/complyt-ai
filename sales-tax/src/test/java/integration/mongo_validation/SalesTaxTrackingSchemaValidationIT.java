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
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
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
    public void saveSalesTaxTracking_invalidComplytId_Failure() {
        salesTaxTrackingDocument.put("complytId", "invalid_uuid");
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
    public void saveSalesTaxTracking_invalidTenantId_Failure() {
        salesTaxTrackingDocument.put("tenantId", 12345);
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
    public void saveSalesTaxTracking_invalidEnforcesSalesTax_Failure() {
        salesTaxTrackingDocument.put("enforcesSalesTax", "true");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("enforcesSalesTax"))
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
    public void saveSalesTaxTracking_invalidApproved_Failure() {
        salesTaxTrackingDocument.put("approved", "true");
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
    public void saveSalesTaxTracking_invalidApprovalDate_Failure() {
        salesTaxTrackingDocument.put("approvalDate", "2024-01-01");
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
    public void saveSalesTaxTracking_invalidAppliedDate_Failure() {
        salesTaxTrackingDocument.put("appliedDate", "2024-01-01");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("appliedDate"))
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
    public void saveSalesTaxTracking_invalidPhysicalNexusTracker_Failure() {
        salesTaxTrackingDocument.put("physicalNexusTracker", "invalid_object");
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
    public void saveSalesTaxTracking_invalidEconomicNexusTracker_Failure() {
        salesTaxTrackingDocument.put("economicNexusTracker", "invalid_object");
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
    public void saveSalesTaxTracking_invalidClientTracking_Failure() {
        salesTaxTrackingDocument.put("clientTracking", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxTrackingDocument, "sales_tax_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("clientTracking"))
                .verify();
    }
}
