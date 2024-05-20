package integration.mongo_validation;

import integration.TestContainersInitializerIT;
import io.complyt.files.FilesApplication;
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
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = FilesApplication.class)
@AutoConfigureWebTestClient
public class FileSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document file;

    @DynamicPropertySource
    static void putProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("files"));
    }

    @BeforeEach
    void putup() {
        file = TestUtilities.fileDocument();
    }

    @Test
    public void saveToken_validFile_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void save_InvalidComplytIdFormat_throwsValidationError() {
        // Assuming you have a method to put a non-binary UUID format for testing
        file.put("complytId", "not-a-binary-uuid");

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_MissingTenantId_throwsValidationError() {
        file.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_MissingLink_throwsValidationError() {
        file.remove("link");

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_MissingComplytId_throwsValidationError() {
        file.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_ExceedsMaxLengthTenantId_throwsValidationError() {
        // Create a string longer than 100 characters
        String longString = new String(new char[101]).replace('\0', 'a');
        file.put("tenantId", longString);

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidLinkFormat_throwsValidationError() {
        file.put("link", "htp://badurl.com");

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_ExceedsMaxLengthLink_throwsValidationError() {
        // Create a URL string longer than 2048 characters
        String longUrl = "http://" + new String(new char[2040]).replace('\0', 'a') + ".com";
        file.put("link", longUrl);

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidComplytIdType_throwsValidationError() {
        file.put("complytId", "123"); // Incorrect: String instead of Binary

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidTenantIdType_throwsValidationError() {
        file.put("tenantId", true); // Incorrect tenantId, String

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidLinkType_throwsValidationError() {
        file.put("link", 123); // Incorrect URL format, invalid protocol

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidLinkPattern_throwsValidationError() {
        file.put("link", "htttp:\\notavalidurl"); // Incorrect URL format, invalid protocol

        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void save_InvalidAdditionalProperties_throwsValidationError() {
        file.put("additionalProperty", 123);
        StepVerifier.create(reactiveMongoTemplate.save(file, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}