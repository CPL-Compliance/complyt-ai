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

    Document exemption;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        exemption = ITUtilities.exemptionDocument();
    }

    @Test
    public void saveExemption_validExemption_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveExemption_MissingCustomerId_throwsValidationError() {
        exemption.remove("customerId");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingClassification_throwsValidationError() {
        exemption.remove("classification");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingValidationDates_throwsValidationError() {
        exemption.remove("validationDates");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStatus_throwsValidationError() {
        exemption.remove("status");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingCertificate_throwsValidationError() {
        exemption.remove("certificate");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingExemptionType_throwsValidationError() {
        exemption.remove("exemptionType");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingTenantId_throwsValidationError() {
        exemption.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingComplytId_throwsValidationError() {
        exemption.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingExemptionStatus_throwsValidationError() {
        exemption.remove("exemptionStatus");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) exemption.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) exemption.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidCustomerIdType_throwsValidationError() {
        exemption.put("customerId", "invalid_binary"); // customerId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStateType_throwsValidationError() {
        exemption.put("state", "invalid_state"); // state should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidClassificationType_throwsValidationError() {
        exemption.put("classification", "invalid_classification"); // classification should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidValidationDatesType_throwsValidationError() {
        exemption.put("validationDates", "invalid_dates"); // validationDates should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStatusType_throwsValidationError() {
        exemption.put("status", "invalid_status"); // status should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidCertificateType_throwsValidationError() {
        exemption.put("certificate", "invalid_certificate"); // certificate should be an object

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidExemptionType_throwsValidationError() {
        exemption.put("exemptionType", 123); // exemptionType should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidTenantIdType_throwsValidationError() {
        exemption.put("tenantId", 123); // tenantId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidComplytIdType_throwsValidationError() {
        exemption.put("complytId", "invalid_binary"); // complytId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidExemptionStatusType_throwsValidationError() {
        exemption.put("exemptionStatus", 123); // exemptionStatus should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidInternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) exemption.get("internalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidInternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) exemption.get("internalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidCertificateIdType_throwsValidationError() {
        ((Document) exemption.get("certificate")).put("certificateId", 123); // certificateId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingCertificateId_throwsValidationError() {
        ((Document) exemption.get("certificate")).remove("certificateId"); // certificateId is required

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStatusCodeType_throwsValidationError() {
        ((Document) exemption.get("status")).put("code", 123); // code should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStatusCode_throwsValidationError() {
        ((Document) exemption.get("status")).remove("code"); // code is required

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidStateAbbreviationType_throwsValidationError() {
        ((Document) exemption.get("state")).put("abbreviation", 123); // abbreviation should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingStateAbbreviation_throwsValidationError() {
        ((Document) exemption.get("state")).remove("abbreviation"); // abbreviation is required

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidClassificationCodeType_throwsValidationError() {
        ((Document) exemption.get("classification")).put("code", 123); // code should be a string

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingClassificationCode_throwsValidationError() {
        ((Document) exemption.get("classification")).remove("code"); // code is required

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_InvalidValidationDatesFromDateType_throwsValidationError() {
        ((Document) exemption.get("validationDates")).put("fromDate", "invalid_date"); // fromDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveExemption_MissingValidationDatesFromDate_throwsValidationError() {
        ((Document) exemption.get("validationDates")).remove("fromDate"); // fromDate is required

        StepVerifier.create(reactiveMongoTemplate.save(exemption, "exemption"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}



