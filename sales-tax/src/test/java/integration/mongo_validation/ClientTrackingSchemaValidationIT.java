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
public class ClientTrackingSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document clientTracking;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        clientTracking = ITUtilities.clientTrackingDocument();
    }

    @Test
    public void saveClientTracking_validSClientTracking_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveClientTracking_MissingNexus_throwsValidationError() {
        clientTracking.remove("nexus");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingTenantId_throwsValidationError() {
        clientTracking.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingName_throwsValidationError() {
        clientTracking.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingInternalTimestamps_throwsValidationError() {
        clientTracking.remove("internalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingInternalTimestampsCreatedDate_throwsValidationError() {
        ((Document) clientTracking.get("internalTimestamps")).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingInternalTimestampsUpdatedDate_throwsValidationError() {
        ((Document) clientTracking.get("internalTimestamps")).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingNexusTaxableDate_throwsValidationError() {
        ((Document) clientTracking.get("nexus")).remove("taxableDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidTenantIdType_throwsValidationError() {
        clientTracking.put("tenantId", 123);

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidNameType_throwsValidationError() {
        clientTracking.put("name", 123);

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidInternalTimestampsCreatedDateType_throwsValidationError() {
        ((Document) clientTracking.get("internalTimestamps")).put("createdDate", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidInternalTimestampsUpdatedDateType_throwsValidationError() {
        ((Document) clientTracking.get("internalTimestamps")).put("updatedDate", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidNexusTaxableDateType_throwsValidationError() {
        ((Document) clientTracking.get("nexus")).put("taxableDate", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_AdditionalProperty_throwsValidationError() {
        clientTracking.append("extraField", "extraValue"); // Adding an extra field

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidNexusType_throwsValidationError() {
        clientTracking.put("nexus", "invalidType"); // nexus should be an object

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidSubsidiariesType_throwsValidationError() {
        clientTracking.put("subsidiaries", "invalidType");

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidSubsidiaryItemType_throwsValidationError() {
        clientTracking.put("subsidiaries", new Document("subsidiaryId", 123));

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidClassType_throwsValidationError() {
        clientTracking.put("_class", 123); // _class should be a string

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidAdditionalProperty_throwsValidationError() {
        clientTracking.put("additionalProperty", 123); // _class should be a string

        StepVerifier.create(reactiveMongoTemplate.save(clientTracking, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
