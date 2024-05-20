package integration.mongo_validation;

import com.complyt.SalesTaxApplication;
import integration.TestContainersInitializerIT;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import testUtils.integration_test.ITUtilities;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class ProductClassificationSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document productClassificationDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        productClassificationDocument = ITUtilities.productClassificationDocument();
    }

    @Test
    public void saveProductClassification_validProductClassification_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveProductClassification_missingTaxCode_Failure() {
        productClassificationDocument.remove("taxCode");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingDescription_Failure() {
        productClassificationDocument.remove("description");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("description"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingTitle_Failure() {
        productClassificationDocument.remove("title");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("title"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingTangibleCategory_Failure() {
        productClassificationDocument.remove("tangibleCategory");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategory"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_Failure() {
        productClassificationDocument.remove("jurisdictionalSalesTaxRules");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalSalesTaxRules"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalTaxRules_Failure() {
        productClassificationDocument.remove("jurisdictionalTaxRules");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalTaxRules"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidIdType_Failure() {
        productClassificationDocument.put("_id", "invalidObjectId");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("_id"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidTitleType_Failure() {
        productClassificationDocument.put("title", 12345);
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("title"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidTaxCodeType_Failure() {
        productClassificationDocument.put("taxCode", 12345);
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidDescriptionType_Failure() {
        productClassificationDocument.put("description", 12345);
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("description"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidTangibleCategoryType_Failure() {
        productClassificationDocument.put("tangibleCategory", 12345);
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategory"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidJurisdictionalSalesTaxRulesType_Failure() {
        productClassificationDocument.put("jurisdictionalSalesTaxRules", "invalidType");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalSalesTaxRules"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidJurisdictionalTaxRulesType_Failure() {
        productClassificationDocument.put("jurisdictionalTaxRules", "invalidType");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalTaxRules"))
                .verify();
    }

    @Test
    public void saveProductClassification_invalidJurisdictionalSalesTaxRulesInnerType_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).put("AL", "invalidType");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("jurisdictionalSalesTaxRules"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_name_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("name");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_taxable_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("taxable");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxable"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_specialTreatment_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("specialTreatment");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("specialTreatment"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_calculationType_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("calculationType");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationType"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_description_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("description");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("description"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_calculationValue_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("calculationValue");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationValue"))
                .verify();
    }

    @Test
    public void saveProductClassification_missingJurisdictionalSalesTaxRules_abbreviation_Failure() {
        ((Document) productClassificationDocument.get("jurisdictionalSalesTaxRules")).get("CA", Document.class).remove("abbreviation");
        StepVerifier.create(reactiveMongoTemplate.save(productClassificationDocument, "product_classification"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("abbreviation"))
                .verify();
    }
}
