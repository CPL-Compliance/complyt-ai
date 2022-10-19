package com.complyt.business.utils.data_injector;

import com.complyt.business.data_injector.TransactionShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionShippingFeeJurisdictionalRulesInjectorTest {

    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        transactionShippingFeeJurisdictionalRulesInjector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = null;
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "C1S1",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee);
    }

    private ShippingFee createShippingFee() {
        return new ShippingFee(false, 0, 1000, null,
                new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f), "C6S1");
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> itemJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification itemProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", itemJurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C1S1", itemProductClassification);
            put("C6S1", shippingProductClassification);
        }};
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, false,
                CalculationType.FIXED, "description", 0, null);
    }


    @Test
    void inject_InjectsDataToTransactionWithShippingFee_ReturnsModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassifications();
        ShippingFee shippingFeeWithRules = transaction.getShippingFee().withJurisdictionalSalesTaxRules(createJurisdictionalSalesTaxRules());

        Transaction transactionWithRules = transaction.withShippingFee(shippingFeeWithRules);

        // When + Then
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithRules).verifyComplete();
    }
}
