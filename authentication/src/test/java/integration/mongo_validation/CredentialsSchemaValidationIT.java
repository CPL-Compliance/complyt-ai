package integration.mongo_validation;

import integration.SecurityConfig;
import integration.TestContainersInitializerIT;
import io.complyt.authentication.AuthenticationApplication;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AuthenticationApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
public class CredentialsSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document credentialsDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @BeforeEach
    void setup() {
        credentialsDocument = TestUtilities.credentialsDocument();;
    }

    @Test
    public void saveCredentials_validCredentials_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveCredentials_MissingComplytClientId_throwsValidationError() {
        credentialsDocument.remove("complytClientId");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingComplytClientSecret_throwsValidationError() {
        credentialsDocument.remove("complytClientSecret");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                . verify();
    }

    @Test
    public void saveCredentials_MissingClientId_throwsValidationError() {
        credentialsDocument.remove("clientId");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientSecret_throwsValidationError() {
        credentialsDocument.remove("clientSecret");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientIdIv_throwsValidationError() {
        credentialsDocument.remove("clientIdIv");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientSecretIv_throwsValidationError() {
        credentialsDocument.remove("clientSecretIv");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingAudience_throwsValidationError() {
        credentialsDocument.remove("audience");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingGrantType_throwsValidationError() {
        credentialsDocument.remove("grantType");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingTenantId_throwsValidationError() {
        credentialsDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingName_throwsValidationError() {
        credentialsDocument.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingStatus_throwsValidationError() {
        credentialsDocument.remove("status");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidTenantIdType_throwsValidationError() {
        credentialsDocument.put("tenantId", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidNameType_throwsValidationError() {
        credentialsDocument.put("name", 456);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidStatusType_throwsValidationError() {
        credentialsDocument.put("status", 123);
        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientIdType_throwsValidationError() {
        credentialsDocument.put("clientId", false);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientSecretIvType_throwsValidationError() {
        credentialsDocument.put("clientSecretIv", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidAudienceType_throwsValidationError() {
        credentialsDocument.put("audience", 789);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidGrantType_throwsValidationError() {
        credentialsDocument.put("grantType", new Date());

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientSecretType_throwsValidationError() {
        credentialsDocument.put("clientSecret", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidComplytClientIdType_throwsValidationError() {
        credentialsDocument.put("complytClientId", new Date());

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidComplytClientSecretType_throwsValidationError() {
        credentialsDocument.put("complytClientSecret", true);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientIdIvType_throwsValidationError() {
        credentialsDocument.put("clientIdIv", 456);

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidExpireAtType_throwsValidationError() {
        credentialsDocument.put("expireAt", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidAdditionalProperties_throwsValidationError() {
        credentialsDocument.put("additionalProperty", 123);
        
        StepVerifier.create(reactiveMongoTemplate.save(credentialsDocument, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
