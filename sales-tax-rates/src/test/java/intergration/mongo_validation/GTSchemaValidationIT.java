package intergration.mongo_validation;

import com.complyt.SalesTaxRatesApplication;
import com.example.complyt.config.SecurityConfig;
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
public class GTSchemaValidationIT extends MongoContainerInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document gtRates;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @BeforeEach
    void setup() {
        gtRates = TestUtilities.gtRatesDocument();
    }

    @Test
    public void saveGTRates_validGTRates_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveGTRates_MissingGtAddress_throwsValidationError() {
        gtRates.remove("gtAddress");

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtRates_throwsValidationError() {
        gtRates.remove("gtRates");

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtRatesCountryRate_throwsValidationError() {
        ((Document) gtRates.get("gtRates")).remove("countryRate");

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtRatesTaxRate_throwsValidationError() {
        ((Document) gtRates.get("gtRates")).remove("taxRate");

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_MissingGtAddressCountry_throwsValidationError() {
        ((Document) gtRates.get("gtAddress")).remove("country");

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtAddressCountryType_throwsValidationError() {
        ((Document) gtRates.get("gtAddress")).put("country", 123);

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtRatesTaxRateType_throwsValidationError() {
        ((Document) gtRates.get("gtRates")).put("taxRate", 0.18);

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtRatesCountryRateType_throwsValidationError() {
        ((Document) gtRates.get("gtRates")).put("countryRate", 0.18);

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtRatesRegionRateType_throwsValidationError() {
        ((Document) gtRates.get("gtRates")).put("regionRate", 0.05);

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveGTRates_InvalidGtAddressRegionType_throwsValidationError() {
        ((Document) gtRates.get("gtAddress")).put("region", 123);

        StepVerifier.create(reactiveMongoTemplate.save(gtRates, "gt_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
