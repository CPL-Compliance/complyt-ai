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
import reactor.core.publisher.Mono;
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
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        customerDocument = ITUtilities.customerDocument();
    }

    @Test
    public void saveCustomer_validCustomer_Success() {
        Mono<Document> savedDocument = reactiveMongoTemplate.save(customerDocument, "customer");

        StepVerifier.create(savedDocument)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveCustomer_MissingRequiredComplytId_throwsValidationError() {
        customerDocument.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredExternalId_throwsValidationError() {
        customerDocument.remove("externalId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredSource_throwsValidationError() {
        customerDocument.remove("source");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredTenantId_throwsValidationError() {
        customerDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredCustomerType_throwsValidationError() {
        customerDocument.remove("customerType");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredInternalTimestamps_throwsValidationError() {
        customerDocument.remove("internalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredExternalTimestamps_throwsValidationError() {
        customerDocument.remove("externalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customerDocument.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredExternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_MissingRequiredExternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) customerDocument.get("externalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCustomer_mainSchema_withAdditionalPropertyFalse_Failure() {
        customerDocument.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveCustomer_address_withAdditionalPropertyFalse_Failure() {
        Document address = (Document) customerDocument.get("address");
        address.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveCustomer_internalTimestamps_withAdditionalPropertyFalse_Failure() {
        Document internalTimestamps = (Document) customerDocument.get("internalTimestamps");
        internalTimestamps.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveCustomer_externalTimestamps_withAdditionalPropertyFalse_Failure() {
        Document externalTimestamps = (Document) customerDocument.get("externalTimestamps");
        externalTimestamps.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
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
    public void saveCustomer_InvalidComplytIdType_throwsValidationError() {
        customerDocument.put("complytId", "invalid_binary"); // complytId should be binary

        StepVerifier.create(reactiveMongoTemplate.save(customerDocument, "customer"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }



}
