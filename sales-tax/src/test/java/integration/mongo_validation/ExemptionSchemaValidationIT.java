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
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
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
    public void saveExemption_MissingCustomerId_throwsValidationError() {
        exemptionDocument.remove("customerId");

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
    public void saveExemption_MissingTenantId_throwsValidationError() {
        exemptionDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingComplytId_throwsValidationError() {
        exemptionDocument.remove("complytId");

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

    @Test
    public void saveExemption_InvalidStatusType_throwsValidationError() {
        exemptionDocument.put("status", "invalid_status"); // status should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidCertificateType_throwsValidationError() {
        exemptionDocument.put("certificate", "invalid_certificate"); // certificate should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidExemptionType_throwsValidationError() {
        exemptionDocument.put("exemptionType", 123); // exemptionType should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidTenantIdType_throwsValidationError() {
        exemptionDocument.put("tenantId", 123); // tenantId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidComplytIdType_throwsValidationError() {
        exemptionDocument.put("complytId", "invalid_binary"); // complytId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidExemptionStatusType_throwsValidationError() {
        exemptionDocument.put("exemptionStatus", 123); // exemptionStatus should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidInternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) exemptionDocument.get("internalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidInternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) exemptionDocument.get("internalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidCertificateIdType_throwsValidationError() {
        ((Document) exemptionDocument.get("certificate")).put("certificateId", 123); // certificateId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingCertificateId_throwsValidationError() {
        ((Document) exemptionDocument.get("certificate")).remove("certificateId"); // certificateId is required

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStatusCodeType_throwsValidationError() {
        ((Document) exemptionDocument.get("status")).put("code", 123); // code should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStatusCode_throwsValidationError() {
        ((Document) exemptionDocument.get("status")).remove("code"); // code is required

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStateAbbreviationType_throwsValidationError() {
        ((Document) exemptionDocument.get("state")).put("abbreviation", 123); // abbreviation should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStateAbbreviation_throwsValidationError() {
        ((Document) exemptionDocument.get("state")).remove("abbreviation"); // abbreviation is required

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidClassificationCodeType_throwsValidationError() {
        ((Document) exemptionDocument.get("classification")).put("code", 123); // code should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingClassificationCode_throwsValidationError() {
        ((Document) exemptionDocument.get("classification")).remove("code"); // code is required

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidValidationDatesFromDateType_throwsValidationError() {
        ((Document) exemptionDocument.get("validationDates")).put("fromDate", "invalid_date"); // fromDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingValidationDatesFromDate_throwsValidationError() {
        ((Document) exemptionDocument.get("validationDates")).remove("fromDate"); // fromDate is required

        StepVerifier.create(reactiveMongoTemplate.save(exemptionDocument, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}



