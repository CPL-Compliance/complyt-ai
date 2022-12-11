package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaxableCollectionAmountExtractorTest {

    TaxableCollectionAmountExtractor taxableCollectionAmountExtractor;

    @Mock
    QualificationChecker qualificationChecker;

    Transaction transaction;
    NexusStateRule nexusStateRule;
    Customer customer;
    ObjectId customerId;

    String nexusStateRuleId;

    @BeforeEach
    void setUp() {
        nexusStateRuleId = UUID.randomUUID().toString();
        customer = createCustomer();
        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
        List<Taxable> taxables = createTaxables();
        taxableCollectionAmountExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, taxables, nexusStateRule);
    }

    private List<Taxable> createTaxables() {
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
        taxables.add(transaction.getShippingFee());
        return taxables;
    }

    private Customer createCustomer() {
        customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, tenantId, CustomerType.RETAIL);
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(nexusStateRuleId, true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE, shippingFee, null);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.TANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    @Test
    void extract_ExtractsTransactionItemsAmount_ReturnsAmount() {
        // Given
        ShippingFee nullShippingFee = null;
        Transaction transactionWithNoShippingFee = transaction.withShippingFee(nullShippingFee);

        // When
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(1), nexusStateRule)).thenReturn(false);
        float expectedAmount = transactionWithNoShippingFee.getItems().get(0).getTotalPrice();
        float amount = taxableCollectionAmountExtractor.extract();

        // Then
        assertEquals(amount, expectedAmount);
    }

    @Test
    void extract_ExtractsTransactionItemsAndShippingFeeAmount_ReturnsAmount() {
        // Given

        // When
        when(qualificationChecker.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transaction.getItems().get(1), nexusStateRule)).thenReturn(false);
        when(qualificationChecker.isQualified(transaction.getShippingFee(), nexusStateRule)).thenReturn(true);
        float amount = taxableCollectionAmountExtractor.extract();
        float expectedAmount = transaction.getItems().get(0).getTotalPrice() + transaction.getShippingFee().getTotalPrice();

        // Then
        assertEquals(amount, expectedAmount);
    }

    @Test
    void equals_sameExtractor_ReturnsTrue() {
        // Given
        TaxableCollectionAmountExtractor givenTaxableCollectionAmountExtractor =
                new TaxableCollectionAmountExtractor(qualificationChecker, createTaxables(), nexusStateRule);

        // When
        boolean isEquals = taxableCollectionAmountExtractor.equals(givenTaxableCollectionAmountExtractor);

        // Then
        assertTrue(isEquals);
    }
}
