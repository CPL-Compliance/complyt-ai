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
public class NexusStateRuleSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document nexusStateRuleDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        nexusStateRuleDocument = ITUtilities.nexusStateRuleDocument();
    }

    @Test
    public void saveNexusStateRule_validNexusStateRule_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveNexusStateRule_missingEnforcesSalesTax_Failure() {
        nexusStateRuleDocument.remove("enforcesSalesTax");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("enforcesSalesTax"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingCountry_Failure() {
        nexusStateRuleDocument.remove("country");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("country"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingTaxableCategories_Failure() {
        nexusStateRuleDocument.remove("taxableCategories");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingTangibleCategories_Failure() {
        nexusStateRuleDocument.remove("tangibleCategories");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingCustomerTypes_Failure() {
        nexusStateRuleDocument.remove("customerTypes");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerTypes"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingTimeFrame_Failure() {
        nexusStateRuleDocument.remove("timeFrame");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("timeFrame"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_missingAppliedDate_Failure() {
        nexusStateRuleDocument.remove("appliedDate");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("appliedDate"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidCountryType_Failure() {
        nexusStateRuleDocument.put("country", 123);
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("country"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidAppliedDateType_Failure() {
        nexusStateRuleDocument.put("appliedDate", "invalid_date");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("appliedDate"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidTimeFrameType_Failure() {
        nexusStateRuleDocument.put("timeFrame", 123);
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("timeFrame"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidTangibleCategoriesType_Failure() {
        nexusStateRuleDocument.put("tangibleCategories", "invalid_array");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidTangibleCategoryItemType_Failure() {
        nexusStateRuleDocument.put("tangibleCategories", 123);
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidCustomerTypesType_Failure() {
        nexusStateRuleDocument.put("customerTypes", "invalid_array");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerTypes"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidCustomerTypeItemType_Failure() {
        nexusStateRuleDocument.put("customerTypes", 123);
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerTypes"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidNexusThresholdType_Failure() {
        nexusStateRuleDocument.put("nexusThreshold", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("nexusThreshold"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidNexusThresholdAmountType_Failure() {
        Document invalidNexusThreshold = new Document("amount", 100000)
                .append("count", 200)
                .append("definition", "AMOUNT_AND_COUNT");
        nexusStateRuleDocument.put("nexusThreshold", invalidNexusThreshold);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("nexusThreshold"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidNexusThresholdCountType_Failure() {
        Document invalidNexusThreshold = new Document("amount", "100000")
                .append("count", "invalid_int")
                .append("definition", "AMOUNT_AND_COUNT");
        nexusStateRuleDocument.put("nexusThreshold", invalidNexusThreshold);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("nexusThreshold"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidNexusThresholdDefinitionType_Failure() {
        Document invalidNexusThreshold = new Document("amount", "100000")
                .append("count", 200)
                .append("definition", 123);
        nexusStateRuleDocument.put("nexusThreshold", invalidNexusThreshold);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("nexusThreshold"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidStateType_Failure() {
        nexusStateRuleDocument.put("state", "invalid_object");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("state"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidStateNameType_Failure() {
        Document invalidState = new Document("abbreviation", "GA")
                .append("name", 123)
                .append("code", "13");
        nexusStateRuleDocument.put("state", invalidState);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("state"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidStateAbbreviationType_Failure() {
        Document invalidState = new Document("abbreviation", 123)
                .append("name", "Georgia")
                .append("code", "13");
        nexusStateRuleDocument.put("state", invalidState);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("state"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidStateCodeType_Failure() {
        Document invalidState = new Document("abbreviation", "GA")
                .append("name", "Georgia")
                .append("code", 123);
        nexusStateRuleDocument.put("state", invalidState);

        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("state"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidTaxableCategoriesType_Failure() {
        nexusStateRuleDocument.put("taxableCategories", "invalid_array");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidTaxableCategoryItemType_Failure() {
        nexusStateRuleDocument.put("taxableCategories", 123);
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableCategories"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_invalidEnforcesSalesTaxType_Failure() {
        nexusStateRuleDocument.put("enforcesSalesTax", "invalid_bool");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("enforcesSalesTax"))
                .verify();
    }
}
