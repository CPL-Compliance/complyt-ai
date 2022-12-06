package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxAggregatorTest {

    SalesTaxAggregator salesTaxAggregator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        salesTaxAggregator = new SalesTaxAggregator();
        transaction = createTransaction();
    }

    private List<Taxable> createTaxables() {
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
        taxables.add(transaction.getShippingFee());
        return taxables;
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private List<Item> createItems() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        return new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private Transaction createTransaction() {
        String id = null;
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = createItems();
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee);
    }

    @Test
    void aggregate_SalesTaxCalculatedForBothItemsAndShippingFee_SalesTaxAmountReturned() {
        // Given
        float expectedItemsSalesTaxAmount = transaction.getItems().stream().map(item -> item.getSalesTaxRate().getTaxRate() * item.getTotalPrice()).reduce(Float::sum).get();
        float expectedShippingFeeSalesTaxAmount = transaction.getShippingFee().getSalesTaxRate().getTaxRate() * transaction.getShippingFee().getTotalPrice();
        float expectedAmount = expectedItemsSalesTaxAmount + expectedShippingFeeSalesTaxAmount;
        List<Taxable> taxAbles = createTaxables();

        // When
        float actualAmount = salesTaxAggregator.aggregate(taxAbles);

        // Then
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void createTaxableCollectionAmountExtractor_NullNexusStateRulePassed_ThrowsException() {
        // Given
        List<Taxable> nullTaxAbles = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxAggregator.aggregate(nullTaxAbles);
        });

        // Then
        assertEquals("taxables is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTaxAggregator()";

        // When
        String actualString = salesTaxAggregator.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void equals_SameSalesTaxAggregator_ReturnsTrue() {
        // Given
        SalesTaxAggregator actualSalesTaxAggregator = new SalesTaxAggregator();

        // When
        boolean isEquals = salesTaxAggregator.equals(actualSalesTaxAggregator);

        // Then
        assertTrue(isEquals);
    }
}
