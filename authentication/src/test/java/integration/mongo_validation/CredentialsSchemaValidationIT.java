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

    Document credentials;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @BeforeEach
    void setup() {
        credentials = TestUtilities.credentialsDocument();;
    }

    @Test
    public void saveCredentials_validCredentials_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveCredentials_MissingComplytClientId_throwsValidationError() {
        credentials.remove("complytClientId");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingComplytClientSecret_throwsValidationError() {
        credentials.remove("complytClientSecret");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                . verify();
    }

    @Test
    public void saveCredentials_MissingClientId_throwsValidationError() {
        credentials.remove("clientId");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientSecret_throwsValidationError() {
        credentials.remove("clientSecret");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientIdIv_throwsValidationError() {
        credentials.remove("clientIdIv");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingClientSecretIv_throwsValidationError() {
        credentials.remove("clientSecretIv");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingAudience_throwsValidationError() {
        credentials.remove("audience");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingGrantType_throwsValidationError() {
        credentials.remove("grantType");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingTenantId_throwsValidationError() {
        credentials.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingName_throwsValidationError() {
        credentials.remove("name");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_MissingStatus_throwsValidationError() {
        credentials.remove("status");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidTenantIdType_throwsValidationError() {
        credentials.put("tenantId", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidNameType_throwsValidationError() {
        credentials.put("name", 456);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidStatusType_throwsValidationError() {
        credentials.put("status", 123);
        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientIdType_throwsValidationError() {
        credentials.put("clientId", false);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientSecretIvType_throwsValidationError() {
        credentials.put("clientSecretIv", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidAudienceType_throwsValidationError() {
        credentials.put("audience", 789);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidGrantType_throwsValidationError() {
        credentials.put("grantType", new Date());

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientSecretType_throwsValidationError() {
        credentials.put("clientSecret", 123);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidComplytClientIdType_throwsValidationError() {
        credentials.put("complytClientId", new Date());

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidComplytClientSecretType_throwsValidationError() {
        credentials.put("complytClientSecret", true);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidClientIdIvType_throwsValidationError() {
        credentials.put("clientIdIv", 456);

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidExpireAtType_throwsValidationError() {
        credentials.put("expireAt", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveCredentials_InvalidAdditionalProperties_throwsValidationError() {
        credentials.put("additionalProperty", 123);
        StepVerifier.create(reactiveMongoTemplate.save(credentials, "credentials"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
