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
public class CustomerSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document customerDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        customerDocument = ITUtilities.customerDocument();
    }

    @Test
    public void saveCustomer_validCustomer_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveCustomer_MissingExternalId_throwsValidationError() {
        customerDocument.remove("externalId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingSource_throwsValidationError() {
        customerDocument.remove("source");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingComplytId_throwsValidationError() {
        customerDocument.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingName_throwsValidationError() {
        customerDocument.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingTenantId_throwsValidationError() {
        customerDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingCustomerType_throwsValidationError() {
        customerDocument.remove("customerType");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestamps_throwsValidationError() {
        customerDocument.remove("internalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestamps_throwsValidationError() {
        customerDocument.remove("externalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalIdType_throwsValidationError() {
        customerDocument.put("externalId", 1586); // externalId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidSourceType_throwsValidationError() {
        customerDocument.put("source", 1); // source should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidComplytIdType_throwsValidationError() {
        customerDocument.put("complytId", "invalid_binary"); // complytId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidNameType_throwsValidationError() {
        customerDocument.put("name", 123); // name should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidTenantIdType_throwsValidationError() {
        customerDocument.put("tenantId", 123); // tenantId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidCustomerType_throwsValidationError() {
        customerDocument.put("customerType", 123); // customerType should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidCustomerTypeValue_throwsValidationError() {
        customerDocument.put("customerType", "INVALID_TYPE"); // customerType should be one of the enum values

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidInternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidInternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidAddressCountryType_throwsValidationError() {
        ((Document) customerDocument.get("address")).put("country", 123); // country should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidAddressIsPartialType_throwsValidationError() {
        ((Document) customerDocument.get("address")).put("isPartial", "true"); // isPartial should be a bool

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
