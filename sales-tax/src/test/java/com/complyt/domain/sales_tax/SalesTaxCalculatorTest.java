package com.complyt.domain.sales_tax;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.checker.ItemsTaxableCheck;
import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxCalculatorTest {

    @InjectMocks
    SalesTaxCalculator salesTaxCalculator;

    @Mock
    ItemsTaxableCheck itemsTaxableCheck;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1");
    }

    @Test
    void calculate_SalesTaxBeingCalculated_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);

        List<Item> items = new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = 0;

        // When
        for (Item item : items) {
            amount += salesTaxRate.getTaxRate() * item.getTotalPrice();
        }

        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, null);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);

    }

    @Test
    void calculate_TransactionComesWithItemWithManualSalesTax_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        List<Item> items = new ArrayList<Item>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", null, salesTaxRate, true, 0.5f, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = items.get(0).getTotalPrice() * 0.5f;

        // When
        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, null);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);

    }

    @Test
    void calculate_JurisdictionalSalesTaxRulesIsCalculatedByPercentage_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesByPercentage = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.PERCENTAGE);
        List<Item> items = new ArrayList<Item>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode",
                    jurisdictionalSalesTaxRulesByPercentage, salesTaxRate, false, 0.5f, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float partialTotalPrice = items.get(0).getTotalPrice() * jurisdictionalSalesTaxRulesByPercentage.getCalculationValue();
        float amount = partialTotalPrice * items.get(0).getSalesTaxRate().getTaxRate();

        // When
        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, null);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_ShippingFeeDoesNotHaveSalesTaxBecauseNoTaxableItems_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        ShippingFee shippingFee = createShippingFee();
        List<Item> items = new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = 0;
        for (Item item : items) {
            amount += salesTaxRate.getTaxRate() * item.getTotalPrice();
        }

        // When
        when(itemsTaxableCheck.hasTaxableItem(items)).thenReturn(false);

        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, shippingFee);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_ShippingFeeHasSalesTax_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        ShippingFee shippingFee = createShippingFee();
        List<Item> items = new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = 0;
        for (Item item : items) {
            amount += salesTaxRate.getTaxRate() * item.getTotalPrice();
        }
        amount += shippingFee.getSalesTaxRate().getTaxRate() * shippingFee.getPrice();

        // When
        when(itemsTaxableCheck.hasTaxableItem(items)).thenReturn(true);

        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, shippingFee);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_ShippingFeeHasManualSalesTax_SalesTaxAmountReturned() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.1f, 0.5f);
        ShippingFee shippingFee = createShippingFee().withManualSalesTax(true).withManualSalesTaxRate(0.8f);
        List<Item> items = new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = 0;
        for (Item item : items) {
            amount += salesTaxRate.getTaxRate() * item.getTotalPrice();
        }
        amount += shippingFee.getManualSalesTaxAmount();

        // When
        when(itemsTaxableCheck.hasTaxableItem(items)).thenReturn(true);

        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items, shippingFee);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Item> nullItems = null;
        ShippingFee shippingFee = createShippingFee();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxCalculator.calculate(nullItems, shippingFee));

        // Then
        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }
}
