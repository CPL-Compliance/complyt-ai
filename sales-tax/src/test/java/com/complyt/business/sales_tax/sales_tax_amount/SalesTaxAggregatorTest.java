package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
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

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        salesTaxAggregator = new SalesTaxAggregator();
        transaction = testUtilities.createTransaction(null)
                .withItems(testUtilities.createItemsWithSalesTaxRate(true, true))
                .withShippingFee(testUtilities.createShippingFeeWithSalesTaxRates(true, true));
    }

    @Test
    void aggregate_SalesTaxCalculatedForBothItemsAndShippingFee_SalesTaxAmountReturned() {
        // Given
        float expectedItemsSalesTaxAmount = transaction.getItems().stream().map(item -> item.getSalesTaxRates().taxRate() * item.getTotalPrice()).reduce(Float::sum).get();
        float expectedShippingFeeSalesTaxAmount = transaction.getShippingFee().getSalesTaxRates().taxRate() * transaction.getShippingFee().getTotalPrice();
        float expectedAmount = expectedItemsSalesTaxAmount + expectedShippingFeeSalesTaxAmount;
        List<Taxable> taxAbles = testUtilities.createTaxables(transaction);

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
