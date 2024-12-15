package integration.mongo_validation;

import integration.TestContainersInitializerIT;
import io.complyt.AddressValidationApplication;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AddressValidationApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ValidatedAddressValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document address = TestUtilities.createAddressValidationDocument();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("address_validation"));
    }

    @Test
    public void saveValidatedAddress_validDocument_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveValidatedAddress_MissingAddress_throwsValidationError() {
        address.remove("address");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_MissingRequestAddress_throwsValidationError() {
        address.remove("requestAddress");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_MissingCreatedDate_throwsValidationError() {
        address.remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_MissingZip_throwsValidationError() {
        ((Document) address.get("address")).remove("zip");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_MissingScore_throwsValidationError() {
        ((Document) address.get("address")).remove("score");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_MissingCounty_throwsValidationError() {
        ((Document) address.get("address")).remove("county");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }


    @Test
    public void saveValidatedAddress_InvalidZipType_throwsValidationError() {
        ((Document) address.get("address")).put("zip", false);

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_InvalidScoreType_throwsValidationError() {
        ((Document) address.get("address")).put("score", "high");  // Invalid type: should be a double

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_InvalidIsPartialType_throwsValidationError() {
        ((Document) address.get("address")).put("isPartial", "false");  // Invalid type: should be a boolean

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveValidatedAddress_withAdditionalProperty_Failure() {
        address.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(address, "california"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }
}
