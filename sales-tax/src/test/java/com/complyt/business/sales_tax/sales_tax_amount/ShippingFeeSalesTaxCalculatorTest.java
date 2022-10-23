package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.business.sales_tax.checker.TaxableItemExistCheck;
import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
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
public class ShippingFeeSalesTaxCalculatorTest {

    @InjectMocks
    ShippingFeeSalesTaxCalculator shippingFeeSalesTaxCalculator;

    @Mock
    TaxableItemExistCheck taxableItemExistCheck;

    ShippingFee shippingFee;
    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        shippingFee = createShippingFee();
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

    private List<Item> createItems() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        return new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
    }

    @Test
    void calculate_ShippingFeeHasSalesTax_SalesTaxAmountReturned() {
        // Given
        List<Item> items = createItems();
        float amount = 0;

        amount += shippingFee.getSalesTaxRate().getTaxRate() * shippingFee.getPrice();

        // When
        when(taxableItemExistCheck.hasTaxableItem(items)).thenReturn(true);
        float salesTaxAmountReturnedFromCalculation = shippingFeeSalesTaxCalculator.calculate(shippingFee, items);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_ShippingFeeHasManualSalesTax_SalesTaxAmountReturned() {
        // Given
        List<Item> items = createItems();
        float amount = 0;
        ShippingFee shippingFeeWithManualSalesTax = shippingFee.withManualSalesTax(true).withManualSalesTaxRate(0.5f);
        amount += shippingFeeWithManualSalesTax.getManualSalesTaxAmount();

        // When
        when(taxableItemExistCheck.hasTaxableItem(items)).thenReturn(true);

        float salesTaxAmountReturnedFromCalculation = shippingFeeSalesTaxCalculator.calculate(shippingFeeWithManualSalesTax, items);

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

        // When
        when(taxableItemExistCheck.hasTaxableItem(items)).thenReturn(false);
        float salesTaxAmountReturnedFromCalculation = shippingFeeSalesTaxCalculator.calculate(shippingFee, items);

        // Then
        assertEquals(amount, salesTaxAmountReturnedFromCalculation);
    }

    @Test
    void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Item> nullItems = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            shippingFeeSalesTaxCalculator.calculate(shippingFee, nullItems);
        });

        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }

    @Test
    void calculate_NullShippingFeePassed_ThrowsException() {
        // Given
        ShippingFee nullShippingFee = null;
        List<Item> items = createItems();

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            shippingFeeSalesTaxCalculator.calculate(nullShippingFee, items);
        });

        assertEquals(nullPointerException.getMessage(), "shippingFee is marked non-null but is null");
    }
}
