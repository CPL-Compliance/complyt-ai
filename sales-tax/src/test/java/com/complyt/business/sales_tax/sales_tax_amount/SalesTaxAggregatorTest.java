package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
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
                .withItems(testUtilities.createItemsWithSalesTaxRate(true, false, true))
                .withShippingFee(testUtilities.createShippingFeeWithSalesTaxRates(true, true));
    }

    @Test
    void aggregate_SalesTaxCalculatedForBothItemsAndShippingFee_SalesTaxAmountReturned() {
        // Given
        BigDecimal expectedItemsSalesTaxAmount = transaction.getItems().stream().map(item -> item.getSalesTaxRates().taxRate().multiply(item.getCalculatedTotal())).reduce(BigDecimal::add).get();
        BigDecimal expectedShippingFeeSalesTaxAmount = transaction.getShippingFee().getSalesTaxRates().taxRate().multiply(transaction.getShippingFee().getCalculatedTotal());
        BigDecimal expectedAmount = expectedItemsSalesTaxAmount.add(expectedShippingFeeSalesTaxAmount);
        List<Taxable> taxAbles = testUtilities.createTaxables(transaction);

        // When
        BigDecimal actualAmount = salesTaxAggregator.aggregate(taxAbles);

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
