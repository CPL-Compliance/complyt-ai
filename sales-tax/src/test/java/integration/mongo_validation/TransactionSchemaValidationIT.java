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

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class TransactionSchemaValidationIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    Document transactionDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> TestContainersInitializerIT.MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        transactionDocument = ITUtilities.transactionDocument();
    }

    @Test
    public void saveTransaction_validTransaction_Success() {
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveTransaction_missingExternalId_Failure() {
        transactionDocument.remove("externalId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingSource_Failure() {
        transactionDocument.remove("source");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("source"))
                .verify();
    }

    @Test
    public void saveTransaction_missingItems_Failure() {
        transactionDocument.remove("items");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("items"))
                .verify();
    }

    @Test
    public void saveTransaction_missingShippingAddress_Failure() {
        transactionDocument.remove("shippingAddress");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("shippingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_missingCustomerId_Failure() {
        transactionDocument.remove("customerId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("customerId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTransactionStatus_Failure() {
        transactionDocument.remove("transactionStatus");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionStatus"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTenantId_Failure() {
        transactionDocument.remove("tenantId");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tenantId"))
                .verify();
    }

    @Test
    public void saveTransaction_missingInternalTimestamps_Failure() {
        transactionDocument.remove("internalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("internalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingExternalTimestamps_Failure() {
        transactionDocument.remove("externalTimestamps");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("externalTimestamps"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTaxableItemsAmount_Failure() {
        transactionDocument.remove("taxableItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableItemsAmount"))
                .verify();
    }

    @Test
    public void saveTransaction_missingTangibleItemsAmount_Failure() {
        transactionDocument.remove("tangibleItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleItemsAmount"))
                .verify();
    }
    
    @Test
    public void saveTransaction_missingTotalItemsAmount_Failure() {
        transactionDocument.remove("totalItemsAmount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("totalItemsAmount"))
                .verify();
    }

    // Additional Property : False

    @Test
    public void saveTransaction_mainSchema_withAdditionalPropertyFalse_Failure() {
        transactionDocument.put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_billingAddress_withAdditionalPropertyFalse_Failure() {
        transactionDocument.get("billingAddress", Document.class).put("additionalProperty", "someValue");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_shippingAddress_withAdditionalPropertyFalse_Failure() {
        transactionDocument.get("shippingAddress", Document.class).put("additionalProperty", "someValue");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("shippingAddress"))
                .verify();
    }

    @Test
    public void saveTransaction_salesTax_withAdditionalPropertyFalse_Failure() {
        transactionDocument.get("salesTax", Document.class).put("additionalProperty", 123);
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperty"))
                .verify();
    }

    // shippingFee - Sub fields PropertyFalse
    @Test
    public void saveTransaction_shippingFee_withAdditionalPropertyFalse_Failure() {
        transactionDocument.get("shippingFee", Document.class).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_ShippingFee_withAdditionalPropertyInJurisdictionalSalesTaxRules_Failure() {
        transactionDocument.get("shippingFee", Document.class).get("jurisdictionalSalesTaxRules", Document.class).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_ShippingFee_withAdditionalPropertyInJurisdictionalTaxRules_Failure() {
        transactionDocument.get("shippingFee", Document.class).get("jurisdictionalTaxRules", Document.class).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_ShippingFee_withAdditionalPropertyInSalesTaxRates_Failure() {
        transactionDocument.get("shippingFee", Document.class).get("salesTaxRates", Document.class).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_ShippingFee_withAdditionalPropertyInGtRates_Failure() {
        transactionDocument.get("shippingFee", Document.class).get("gtRates", Document.class).put("additionalProperty", "value");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    // Item - Sub fields PropertyFalse
    @Test
    public void saveTransaction_items_withAdditionalPropertyFalse_Failure() {
        Document transactionItem = ITUtilities.transactionItemDocument();
        transactionItem.put("additionalProperty", "value");
        transactionDocument.put("items", List.of(transactionItem));

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_items_withAdditionalPropertyInJurisdictionalSalesTaxRules_Failure() {
        Document transactionItem = ITUtilities.transactionItemDocument();
        transactionItem.get("jurisdictionalSalesTaxRules", Document.class).put("additionalProperty", "value");
        transactionDocument.put("items", List.of(transactionItem));

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_items_withAdditionalPropertyInJurisdictionalTaxRules_Failure() {
        Document transactionItem = ITUtilities.transactionItemDocument();
        transactionItem.get("jurisdictionalTaxRules", Document.class).put("additionalProperty", "value");
        transactionDocument.put("items", List.of(transactionItem));

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_items_withAdditionalPropertyInSalesTaxRates_Failure() {
        Document transactionItem = ITUtilities.transactionItemDocument();
        transactionItem.get("salesTaxRates", Document.class).put("additionalProperty", "value");
        transactionDocument.put("items", List.of(transactionItem));

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    @Test
    public void saveTransaction_items_withAdditionalPropertyInGtRates_Failure() {
        Document transactionItem = ITUtilities.transactionItemDocument();
        transactionItem.get("gtRates", Document.class).put("additionalProperty", "value");
        transactionDocument.put("items", List.of(transactionItem));

        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("additionalProperties"))
                .verify();
    }

    // Required Nested-Sub-Fields
    @Test
    public void saveTransaction_salesTax_MissingAmount_Failure() {
        transactionDocument.get("salesTax", Document.class).remove("amount");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("amount"))
                .verify();
    }

    @Test
    public void saveTransaction_salesTax_MissingTaxCodeInSalesTaxRates_taxRateType_Failure() {
        transactionDocument.get("salesTax", Document.class).get("salesTaxRates", Document.class).remove("taxRate");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxRate"))
                .verify();
    }

    @Test
    public void saveTransaction_salesTax_MissingTaxCodeInGt_taxRateType_Failure() {
        transactionDocument.get("salesTax", Document.class).get("gtRates", Document.class).remove("taxRate");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxRate"))
                .verify();
    }

    @Test
    public void saveTransaction_shippingFee_missingTaxCode_Failure() {
        transactionDocument.get("shippingFee", Document.class).remove("taxCode");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }
    
    
    @Test
    public void saveTransaction_item_missingName_Failure() {
        transactionDocument.getList("items", Document.class).get(0).remove("name");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingTaxCode_Failure() {
        transactionDocument.getList("items", Document.class).get(0).remove("taxCode");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxCode"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingTangibleCategory_Failure() {
        transactionDocument.getList("items", Document.class).get(0).remove("tangibleCategory");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("tangibleCategory"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingItemTaxableCategory_Failure() {
        transactionDocument.getList("items", Document.class).get(0).remove("taxableCategory");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxableCategory"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingCalculatedTotal_Failure() {
        transactionDocument.getList("items", Document.class).get(0).remove("calculatedTotal");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculatedTotal"))
                .verify();
    }


    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingName_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("name");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingAbbreviation_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("abbreviation");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("abbreviation"))
                .verify();
    }

    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingTaxable_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("taxable");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxable"))
                .verify();
    }

    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingSpecialTreatment_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("specialTreatment");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("specialTreatment"))
                .verify();
    }

    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingCalculationType_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("calculationType");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationType"))
                .verify();
    }

    @Test
    public void saveTransaction_jurisdictionalSalesTaxRules_missingCalculationValue_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalSalesTaxRules", Document.class).remove("calculationValue");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationValue"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_name_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("name");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("name"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_abbreviation_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("abbreviation");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("abbreviation"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_taxable_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("taxable");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxable"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_specialTreatment_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("specialTreatment");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("specialTreatment"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_calculationType_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("calculationType");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationType"))
                .verify();
    }

    @Test
    public void saveTransaction_item_missingJurisdictionalTaxRules_calculationValue_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("jurisdictionalTaxRules", Document.class).remove("calculationValue");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("calculationValue"))
                .verify();
    }

    @Test
    public void saveTransaction_item_MissingSalesTaxRateTaxCode_cityRateType_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("salesTaxRates", Document.class).remove("taxRate");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxRate"))
                .verify();
    }

    @Test
    public void saveTransaction_item_MissingGtRatesTaxCode_Failure() {
        transactionDocument.getList("items", Document.class).get(0)
                .get("gtRates", Document.class).remove("taxRate");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("taxRate"))
                .verify();
    }

    @Test
    public void saveTransaction_ShippingAddress_missingCountry_Failure() {
        transactionDocument.get("shippingAddress", Document.class).remove("country");
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("country"))
                .verify();
    }

    // Random Invalid Fields
    @Test
    public void saveTransaction_invalidCreatedFromType_Failure() {
        transactionDocument.put("createdFrom", 123); // Should Be String
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("createdFrom"))
                .verify();
    }

    @Test
    public void saveTransaction_invalidTransactionFilingStatusType_Failure() {
        transactionDocument.put("transactionFilingStatus", "not valid"); // Should Be Bool
        StepVerifier.create(reactiveMongoTemplate.save(transactionDocument, "transaction"))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("transactionFilingStatus"))
                .verify();
    }
}
