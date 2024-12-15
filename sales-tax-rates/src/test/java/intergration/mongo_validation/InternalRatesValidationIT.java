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
public class InternalRatesValidationIT extends MongoContainerInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document internalRateDocument = TestUtilities.internalSalesTaxRates();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @Test
    public void saveInternalRate_validDocument_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveInternalRate_MissingComplytId_throwsValidationError() {
        internalRateDocument.remove("complytId");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_MissingAddress_throwsValidationError() {
        internalRateDocument.remove("address");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_MissingSalesTaxRates_throwsValidationError() {
        internalRateDocument.remove("salesTaxRates");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_MissingEffectiveDates_throwsValidationError() {
        internalRateDocument.remove("effectiveDates");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_MissingInternalSalesTaxRatesMetaData_throwsValidationError() {
        internalRateDocument.remove("internalSalesTaxRatesMetaData");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_MissingCreatedDate_throwsValidationError() {
        internalRateDocument.remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_InvalidComplytIdType_throwsValidationError() {
        internalRateDocument.put("complytId", "invalid_string");  // Invalid type: should be binary UUID

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_InvalidStateType_throwsValidationError() {
        ((Document) internalRateDocument.get("address")).put("state", 123);  // Invalid type: should be a string

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_InvalidCountyType_throwsValidationError() {
        ((Document) internalRateDocument.get("address")).put("county", 123);  // Invalid type: should be a string

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_InvalidZipType_throwsValidationError() {
        ((Document) internalRateDocument.get("address")).put("zip", 90210);  // Invalid type: should be a string

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_InvalidRateType_throwsValidationError() {
        ((Document) internalRateDocument.get("salesTaxRates")).put("stateRate", 0.1);  // Invalid type: should be string

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveInternalRate_withAdditionalProperty_Failure() {
        internalRateDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    // Additional tests for missing required fields from the schema
    @Test
    public void saveInternalRate_MissingCityInAddress_throwsValidationError() {
        ((Document) internalRateDocument.get("address")).remove("city");

        StepVerifier.create(reactiveMongoTemplate.save(internalRateDocument, "alaska_internal_sales_tax_rates"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
