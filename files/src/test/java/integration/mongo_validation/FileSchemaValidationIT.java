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

    Document fileDocument;

    @DynamicPropertySource
    static void putProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("files"));
    }

    @BeforeEach
    void putup() {
        fileDocument = TestUtilities.fileDocument();
    }

    @Test
    public void saveFile_validFile_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveFile_InvalidComplytIdFormat_throwsValidationError() {
        // Assuming you have a method to put a non-binary UUID format for testing
        fileDocument.put("complytId", "not-a-binary-uuid");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_MissingTenantId_throwsValidationError() {
        fileDocument.remove("tenantId");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_MissingLink_throwsValidationError() {
        fileDocument.remove("link");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_MissingComplytId_throwsValidationError() {
        fileDocument.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_ExceedsMaxLengthTenantId_throwsValidationError() {
        // Create a string longer than 100 characters
        String longString = new String(new char[101]).replace('\0', 'a');
        fileDocument.put("tenantId", longString);

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidLinkFormat_throwsValidationError() {
        fileDocument.put("link", "htp://badurl.com");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_ExceedsMaxLengthLink_throwsValidationError() {
        // Create a URL string longer than 2048 characters
        String longUrl = "http://" + new String(new char[2040]).replace('\0', 'a') + ".com";
        fileDocument.put("link", longUrl);

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidComplytIdType_throwsValidationError() {
        fileDocument.put("complytId", "123"); // Incorrect: String instead of Binary

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidTenantIdType_throwsValidationError() {
        fileDocument.put("tenantId", true); // Incorrect tenantId, String

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidLinkType_throwsValidationError() {
        fileDocument.put("link", 123); // Incorrect URL format, invalid protocol

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidLinkPattern_throwsValidationError() {
        fileDocument.put("link", "htttp:\\notavalidurl"); // Incorrect URL format, invalid protocol

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_InvalidAdditionalProperties_throwsValidationError() {
        fileDocument.put("additionalProperty", 123);
        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveFile_mainSchema_withAdditionalPropertyFalse_Failure() {
        fileDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(fileDocument, "file"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }
}