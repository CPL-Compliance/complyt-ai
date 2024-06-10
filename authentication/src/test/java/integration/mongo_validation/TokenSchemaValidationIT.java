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

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AuthenticationApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
public class TokenSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document tokenDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @BeforeEach
    void setup() {
        tokenDocument = TestUtilities.tokenDocument();
    }

    @Test
    public void saveToken_validToken_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectNextCount(1)
                .verifyComplete();
    }
    @Test
    public void saveToken_MissingComplytClientId_throwsValidationError() {
        tokenDocument.remove("complytClientId");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingComplytClientSecret_throwsValidationError() {
        tokenDocument.remove("complytClientSecret");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingAccessToken_throwsValidationError() {
        tokenDocument.remove("accessToken");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingAccessTokenIv_throwsValidationError() {
        tokenDocument.remove("accessTokenIv");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingScope_throwsValidationError() {
        tokenDocument.remove("scope");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingScopeIv_throwsValidationError() {
        tokenDocument.remove("scopeIv");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingExpiresIn_throwsValidationError() {
        tokenDocument.remove("expiresIn");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingTokenType_throwsValidationError() {
        tokenDocument.remove("tokenType");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingCreatedAt_throwsValidationError() {
        tokenDocument.remove("createdAt");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingExpireAt_throwsValidationError() {
        tokenDocument.remove("expireAt");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidComplytClientIdType_throwsValidationError() {
        tokenDocument.put("complytClientId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidComplytClientSecretType_throwsValidationError() {
        tokenDocument.put("complytClientSecret", new Document());
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidAccessTokenType_throwsValidationError() {
        tokenDocument.put("accessToken", false);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidAccessTokenIvType_throwsValidationError() {
        tokenDocument.put("accessTokenIv", 456);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidScopeType_throwsValidationError() {
        tokenDocument.put("scope", 123);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidScopeIvType_throwsValidationError() {
        tokenDocument.put("scopeIv", 789);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidExpiresInType_throwsValidationError() {
        tokenDocument.put("expiresIn", "notAnInteger");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidTokenType_throwsValidationError() {
        tokenDocument.put("tokenType", 123);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidCreatedAtType_throwsValidationError() {
        tokenDocument.put("createdAt", "notADate");
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidExpireAtType_throwsValidationError() {
        tokenDocument.put("expireAt", 123456);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidClassType_throwsValidationError() {
        tokenDocument.put("_class", 123);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidAdditionalProperties_throwsValidationError() {
        tokenDocument.put("additionalProperty", 123);
        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_mainSchema_withAdditionalPropertyFalse_Failure() {
        tokenDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(tokenDocument, "credentials"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }
}