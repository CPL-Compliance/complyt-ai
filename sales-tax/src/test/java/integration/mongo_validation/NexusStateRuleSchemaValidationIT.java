package integration.mongo_validation;

import com.complyt.SalesTaxApplication;
import integration.TestContainersInitializerIT;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
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

import java.util.List;

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
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
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
    public void saveNexusStateRule_mainSchema_withAdditionalPropertyFalse_Failure() {
        nexusStateRuleDocument.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_state_withAdditionalPropertyFalse_Failure() {
        ((Document) nexusStateRuleDocument.get("state")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveNexusStateRule_nexusThreshold_withAdditionalPropertyFalse_Failure() {
        ((Document) nexusStateRuleDocument.get("nexusThreshold")).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(nexusStateRuleDocument, "nexus_state_rule"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
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
}
