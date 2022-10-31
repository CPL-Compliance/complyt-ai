package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.ItemQualificationCheck;
import com.complyt.business.nexus.checker.qualification_check.ShippingFeeQualificationCheck;
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
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
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
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusTransactionAmountAggregatorTest {

    NexusTransactionAmountAggregator nexusTransactionAmountAggregator;

    @Mock
    ItemQualificationCheck itemQualificationCheck;

    @Mock
    ShippingFeeQualificationCheck shippingFeeNexusStateRuleQualificationCheck;

    Transaction transaction;
    NexusStateRule nexusStateRule;
    Customer customer;
    ObjectId customerId;

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
        nexusTransactionAmountAggregator = createNexusTransactionAmountAggregator();
    }

    private NexusTransactionAmountAggregator createNexusTransactionAmountAggregator() {
        return new NexusAmountAggregatorFactory(itemQualificationCheck, shippingFeeNexusStateRuleQualificationCheck)
                .createNexusTransactionAmountAggregator(transaction, nexusStateRule);
    }

    private Customer createCustomer() {
        customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, clientId, CustomerType.RETAIL);
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

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        ObjectId clientId = new ObjectId();
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
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, clientId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE, shippingFee);
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
        when(itemQualificationCheck.isQualified(transactionWithNoShippingFee.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(itemQualificationCheck.isQualified(transactionWithNoShippingFee.getItems().get(1), nexusStateRule)).thenReturn(false);
        float expectedAmount = transactionWithNoShippingFee.getItems().get(0).getTotalPrice();
        float amount = nexusTransactionAmountAggregator.aggregate();

        // Then
        assertEquals(amount, expectedAmount);
    }

    @Test
    void extract_ExtractsTransactionItemsAndShippingFeeAmount_ReturnsAmount() {
        // Given

        // When
        when(itemQualificationCheck.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(itemQualificationCheck.isQualified(transaction.getItems().get(1), nexusStateRule)).thenReturn(false);
        when(shippingFeeNexusStateRuleQualificationCheck.isQualified(transaction.getShippingFee(), nexusStateRule)).thenReturn(true);
        float amount = nexusTransactionAmountAggregator.aggregate();
        float expectedAmount = transaction.getItems().get(0).getTotalPrice() + transaction.getShippingFee().getPrice();

        // Then
        assertEquals(amount, expectedAmount);
    }

}
