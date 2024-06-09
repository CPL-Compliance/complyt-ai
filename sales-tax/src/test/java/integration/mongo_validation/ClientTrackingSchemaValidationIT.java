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

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class ClientTrackingSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document clientTrackingDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        clientTrackingDocument = ITUtilities.clientTrackingDocument();
    }

    @Test
    public void saveClientTracking_validSClientTracking_Success() {
        Mono<Document> savedDocument = reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking");

        StepVerifier.create(savedDocument)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(reactiveMongoTemplate.remove(savedDocument, "client_tracking"))
                .expectNextCount(1).verifyComplete();
    }

    @Test
    public void saveClientTracking_MissingRequiredTenantId_throwsValidationError() {
        clientTrackingDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingRequiredNexus_throwsValidationError() {
        clientTrackingDocument.remove("nexus");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingRequiredName_throwsValidationError() {
        clientTrackingDocument.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingRequiredInternalTimestamps_throwsValidationError() {
        clientTrackingDocument.remove("internalTimestamps");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingRequiredInternalTimestampsCreatedDate_throwsValidationError() {
        clientTrackingDocument.get("internalTimestamps", Document.class).remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_MissingRequiredInternalTimestampsUpdatedDate_throwsValidationError() {
        clientTrackingDocument.get("internalTimestamps", Document.class).remove("updatedDate");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveClientTracking_mainSchema_withAdditionalPropertyFalse_Failure() {
        clientTrackingDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveClientTracking_nexus_withAdditionalPropertyFalse_Failure() {
        Document nexus = (Document) clientTrackingDocument.get("nexus");
        nexus.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveClientTracking_internalTimestamps_withAdditionalPropertyFalse_Failure() {
        Document internalTimestamps = (Document) clientTrackingDocument.get("internalTimestamps");
        internalTimestamps.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidClassType_throwsValidationError() {
        clientTrackingDocument.put("_class", 123); // _class should be a string

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_class"))
                .verify();
    }

    @Test
    public void saveClientTracking_InvalidSubsidiariesItemType_Failure() {
        clientTrackingDocument.put("subsidiaries", List.of(new Document("additionalProperty", "value")));

        StepVerifier.create(reactiveMongoTemplate.save(clientTrackingDocument, "client_tracking"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }
}
