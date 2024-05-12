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

    Document token;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @BeforeEach
    void setup() {
        token = TestUtilities.tokenDocument();
    }

    @Test
    public void saveToken_validToken_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectNextCount(1)
                .verifyComplete();
    }
    @Test
    public void saveToken_MissingComplytClientId_throwsValidationError() {
        token.remove("complytClientId");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingComplytClientSecret_throwsValidationError() {
        token.remove("complytClientSecret");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingAccessToken_throwsValidationError() {
        token.remove("accessToken");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingAccessTokenIv_throwsValidationError() {
        token.remove("accessTokenIv");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingScope_throwsValidationError() {
        token.remove("scope");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingScopeIv_throwsValidationError() {
        token.remove("scopeIv");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingExpiresIn_throwsValidationError() {
        token.remove("expiresIn");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingTokenType_throwsValidationError() {
        token.remove("tokenType");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingCreatedAt_throwsValidationError() {
        token.remove("createdAt");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_MissingExpireAt_throwsValidationError() {
        token.remove("expireAt");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidComplytClientIdType_throwsValidationError() {
        token.put("complytClientId", 123);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidComplytClientSecretType_throwsValidationError() {
        token.put("complytClientSecret", new Document());
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidAccessTokenType_throwsValidationError() {
        token.put("accessToken", false);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidAccessTokenIvType_throwsValidationError() {
        token.put("accessTokenIv", 456);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidScopeType_throwsValidationError() {
        token.put("scope", 123);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidScopeIvType_throwsValidationError() {
        token.put("scopeIv", 789);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidExpiresInType_throwsValidationError() {
        token.put("expiresIn", "notAnInteger");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidTokenType_throwsValidationError() {
        token.put("tokenType", 123);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidCreatedAtType_throwsValidationError() {
        token.put("createdAt", "notADate");
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidExpireAtType_throwsValidationError() {
        token.put("expireAt", 123456);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveToken_InvalidClassType_throwsValidationError() {
        token.put("_class", 123);
        StepVerifier.create(reactiveMongoTemplate.save(token, "token"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}