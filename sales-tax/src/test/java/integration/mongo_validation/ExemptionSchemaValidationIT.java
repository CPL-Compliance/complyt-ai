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
public class ExemptionSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document exemptionDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        exemptionDocument = ITUtilities.exemptionDocument();
    }

    @Test
    public void saveExemption_validExemption_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveExemption_MissingComplytId_throwsValidationError() {
        exemptionDocument.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingTenantId_throwsValidationError() {
        exemptionDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void
    saveExemption_MissingCustomerId_throwsValidationError() {
        exemptionDocument.remove("customerId");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingCountry_throwsValidationError() {
        exemptionDocument.remove("country");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingClassification_throwsValidationError() {
        exemptionDocument.remove("classification");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingValidationDates_throwsValidationError() {
        exemptionDocument.remove("validationDates");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStatus_throwsValidationError() {
        exemptionDocument.remove("status");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingCertificate_throwsValidationError() {
        exemptionDocument.remove("certificate");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingExemptionType_throwsValidationError() {
        exemptionDocument.remove("exemptionType");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingExemptionStatus_throwsValidationError() {
        exemptionDocument.remove("exemptionStatus");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) exemptionDocument.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) exemptionDocument.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_mainSchema_withAdditionalPropertyFalse_Failure() {
        exemptionDocument.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_state_withAdditionalPropertyFalse_Failure() {
        Document state = (Document) exemptionDocument.get("state");
        state.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_classification_withAdditionalPropertyFalse_Failure() {
        Document classification = (Document) exemptionDocument.get("classification");
        classification.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_validationDates_withAdditionalPropertyFalse_Failure() {
        Document validationDates = (Document) exemptionDocument.get("validationDates");
        validationDates.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_internalTimestamps_withAdditionalPropertyFalse_Failure() {
        Document internalTimestamps = (Document) exemptionDocument.get("internalTimestamps");
        internalTimestamps.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_status_withAdditionalPropertyFalse_Failure() {
        Document status = (Document) exemptionDocument.get("status");
        status.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_certificate_withAdditionalPropertyFalse_Failure() {
        Document certificate = (Document) exemptionDocument.get("certificate");
        certificate.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveExemption_InvalidCustomerIdType_throwsValidationError() {
        exemptionDocument.put("customerId", "invalid_binary"); // customerId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStateType_throwsValidationError() {
        exemptionDocument.put("state", "invalid_state"); // state should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidClassificationType_throwsValidationError() {
        exemptionDocument.put("classification", "invalid_classification"); // classification should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidValidationDatesType_throwsValidationError() {
        exemptionDocument.put("validationDates", "invalid_dates"); // validationDates should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}



