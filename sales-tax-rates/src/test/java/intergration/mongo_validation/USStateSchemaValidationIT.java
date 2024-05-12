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
public class USStateSchemaValidationIT extends MongoContainerInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document salesTaxRate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @BeforeEach
    void setup() {
        salesTaxRate = TestUtilities.salesTaxRatesDocument();
    }

    @Test
    public void saveSalesTaxRate_validSalesTaxRate_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveSalesTaxRate_MissingCreatedDate_throwsValidationError() {
        salesTaxRate.remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingExpireAt_throwsValidationError() {
        salesTaxRate.remove("expireAt");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingAddress_throwsValidationError() {
        salesTaxRate.remove("address");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingRequestAddress_throwsValidationError() {
        salesTaxRate.remove("requestAddress");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRates_throwsValidationError() {
        salesTaxRate.remove("salesTaxRates");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesCityRate_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).remove("cityRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesCountyRate_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).remove("countyRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesStateRate_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).remove("stateRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesTaxRate_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).remove("taxRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesCombinedDistrictRate_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).remove("combinedDistrictRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingRatesMetaDataCityDistrictRate_throwsValidationError() {
        ((Document) ((Document) salesTaxRate.get("salesTaxRates")).get("ratesMetaData")).remove("cityDistrictRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    // Similarly, add tests for required fields in requestAddress and address subdocuments
    @Test
    public void saveSalesTaxRate_MissingRequestAddressState_throwsValidationError() {
        ((Document) salesTaxRate.get("requestAddress")).remove("state");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingRequestAddressZip_throwsValidationError() {
        ((Document) salesTaxRate.get("requestAddress")).remove("zip");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingAddressState_throwsValidationError() {
        ((Document) salesTaxRate.get("address")).remove("state");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingAddressZip_throwsValidationError() {
        ((Document) salesTaxRate.get("address")).remove("zip");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidCreatedDateType_throwsValidationError() {
        salesTaxRate.put("createdDate", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidExpireAtType_throwsValidationError() {
        salesTaxRate.put("expireAt", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesType_throwsValidationError() {
        salesTaxRate.put("salesTaxRates", "invalidType");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesCityRateType_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).put("cityRate", 0.02);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesCountyRateType_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).put("countyRate", 0.03);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesStateRateType_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).put("stateRate", 0.04);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesTaxRateType_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).put("taxRate", 0.09);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesCombinedDistrictRateType_throwsValidationError() {
        ((Document) salesTaxRate.get("salesTaxRates")).put("combinedDistrictRate", 0);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidRatesMetaDataCityDistrictRateType_throwsValidationError() {
        ((Document) ((Document) salesTaxRate.get("salesTaxRates")).get("ratesMetaData")).put("cityDistrictRate", 0);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidRequestAddressStateType_throwsValidationError() {
        ((Document) salesTaxRate.get("requestAddress")).put("state", 123);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidRequestAddressZipType_throwsValidationError() {
        ((Document) salesTaxRate.get("requestAddress")).put("zip", 35097);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidAddressStateType_throwsValidationError() {
        ((Document) salesTaxRate.get("address")).put("state", 123);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidAddressZipType_throwsValidationError() {
        ((Document) salesTaxRate.get("address")).put("zip", 35097);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRate, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
