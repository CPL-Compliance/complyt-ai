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

    Document customer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        customer = ITUtilities.customerDocument();
    }

    @Test
    public void saveCustomer_validCustomer_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveCustomer_MissingExternalId_throwsValidationError() {
        customer.remove("externalId");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingSource_throwsValidationError() {
        customer.remove("source");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingComplytId_throwsValidationError() {
        customer.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingName_throwsValidationError() {
        customer.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingTenantId_throwsValidationError() {
        customer.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingCustomerType_throwsValidationError() {
        customer.remove("customerType");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestamps_throwsValidationError() {
        customer.remove("internalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestamps_throwsValidationError() {
        customer.remove("externalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customer.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customer.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customer.get("externalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingExternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customer.get("externalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalIdType_throwsValidationError() {
        customer.put("externalId", 1586); // externalId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidSourceType_throwsValidationError() {
        customer.put("source", 1); // source should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidComplytIdType_throwsValidationError() {
        customer.put("complytId", "invalid_binary"); // complytId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidNameType_throwsValidationError() {
        customer.put("name", 123); // name should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidTenantIdType_throwsValidationError() {
        customer.put("tenantId", 123); // tenantId should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidCustomerType_throwsValidationError() {
        customer.put("customerType", 123); // customerType should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidCustomerTypeValue_throwsValidationError() {
        customer.put("customerType", "INVALID_TYPE"); // customerType should be one of the enum values

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidInternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) customer.get("internalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidInternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) customer.get("internalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) customer.get("externalTimestamps")).put("createdDate", "invalid_date"); // createdDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidExternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) customer.get("externalTimestamps")).put("updatedDate", "invalid_date"); // updatedDate should be a date

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidAddressCountryType_throwsValidationError() {
        ((Document) customer.get("address")).put("country", 123); // country should be a string

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_InvalidAddressIsPartialType_throwsValidationError() {
        ((Document) customer.get("address")).put("isPartial", "true"); // isPartial should be a bool

        StepVerifier.create(reactiveMongoTemplate.save(customer, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
