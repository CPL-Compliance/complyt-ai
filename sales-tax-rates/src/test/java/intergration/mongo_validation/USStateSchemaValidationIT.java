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
public class USStateSchemaValidationIT extends MongoContainerInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document salesTaxRateDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @BeforeEach
    void setup() {
        salesTaxRateDocument = TestUtilities.salesTaxRatesDocument();
    }

    @Test
    public void saveSalesTaxRate_MissingRequestAddressCountry_throwsValidationError() {
        ((Document) salesTaxRateDocument.get("requestAddress")).remove("country");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingAddressCountry_throwsValidationError() {
        ((Document) salesTaxRateDocument.get("address")).remove("country");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesTaxRate_throwsValidationError() {
        ((Document) salesTaxRateDocument.get("salesTaxRates")).remove("taxRate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesRatesMetaDataCityDistrictRate_throwsValidationError() {
        ((Document) ((Document) salesTaxRateDocument.get("salesTaxRates")).get("ratesMetaData")).remove("cityDistrictRate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRatesRatesMetaDataCountyDistrictRate_throwsValidationError() {
        ((Document) ((Document) salesTaxRateDocument.get("salesTaxRates")).get("ratesMetaData")).remove("countyDistrictRate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void  saveSalesTaxRate_validSalesTaxRate_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveSalesTaxRate_MissingCreatedDate_throwsValidationError() {
        salesTaxRateDocument.remove("createdDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingExpireAt_throwsValidationError() {
        salesTaxRateDocument.remove("expireAt");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingAddress_throwsValidationError() {
        salesTaxRateDocument.remove("address");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingSalesTaxRates_throwsValidationError() {
        salesTaxRateDocument.remove("salesTaxRates");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_MissingRatesMetaDataCityDistrictRate_throwsValidationError() {
        ((Document) ((Document) salesTaxRateDocument.get("salesTaxRates")).get("ratesMetaData")).remove("cityDistrictRate");

                StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidCreatedDateType_throwsValidationError() {
        salesTaxRateDocument.put("createdDate", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidExpireAtType_throwsValidationError() {
        salesTaxRateDocument.put("expireAt", "invalidDate");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }


    @Test
    public void saveSalesTaxRate_mainSchema_withAdditionalPropertyFalse_Failure() {
        salesTaxRateDocument.put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxRate_salesTaxRates_withAdditionalPropertyFalse_Failure() {
        salesTaxRateDocument.get("salesTaxRates", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxRate_ratesMetaData_withAdditionalPropertyFalse_Failure() {
        salesTaxRateDocument.get("salesTaxRates", Document.class)
                .get("ratesMetaData", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxRate_requestAddress_withAdditionalPropertyFalse_Failure() {
        salesTaxRateDocument.get("requestAddress", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveSalesTaxRate_address_withAdditionalPropertyFalse_Failure() {
        salesTaxRateDocument.get("address", Document.class).put("additionalProperty", "value");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }




    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesType_throwsValidationError() {
        salesTaxRateDocument.put("salesTaxRates", "invalidType");

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    public void saveSalesTaxRate_InvalidSalesTaxRatesCityRateType_throwsValidationError() {
        ((Document) salesTaxRateDocument.get("salesTaxRates")).put("cityRate", 0.02);

        StepVerifier.create(reactiveMongoTemplate.save(salesTaxRateDocument, "alabama"))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }
}
