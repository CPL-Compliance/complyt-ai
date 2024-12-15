package intergration.mongo_validation;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.config.SecurityConfig;
import intergration.MongoContainerInitializerIT;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
@ActiveProfiles({"stubInternalRates"})
public class GTSchemaValidationIT extends MongoContainerInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document gtRatesDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @BeforeEach
    void setup() {
        gtRatesDocument = TestUtilities.gtRatesDocument();
    }

    @Test
    public void saveGTRates_validGTRates_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveGTRates_MissingGtAddress_throwsValidationError() {
        gtRatesDocument.remove("gtAddress");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtRates_throwsValidationError() {
        gtRatesDocument.remove("gtRates");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtRatesTaxRate_throwsValidationError() {
        ((Document) gtRatesDocument.get("gtRates")).remove("taxRate");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtAddressCountry_throwsValidationError() {
        ((Document) gtRatesDocument.get("gtAddress")).remove("country");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtAddressCountryType_throwsValidationError() {
        ((Document) gtRatesDocument.get("gtAddress")).put("country", 123);

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_mainSchema_withAdditionalPropertyFalse_Failure() {
        gtRatesDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveGTRates_gtRates_withAdditionalPropertyFalse_Failure() {
        gtRatesDocument.get("gtRates", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveGTRates_gtAddress_withAdditionalPropertyFalse_Failure() {
        gtRatesDocument.get("gtAddress", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }


    @Test
    public void saveGTRates_InvalidGtAddressRegionType_throwsValidationError() {
        ((Document) gtRatesDocument.get("gtAddress")).put("region", 123);

        StepVerifier.create(reactiveMongoTemplate.save(gtRatesDocument, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
