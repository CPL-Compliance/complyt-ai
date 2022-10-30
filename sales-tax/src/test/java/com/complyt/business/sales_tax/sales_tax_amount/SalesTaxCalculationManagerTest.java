package com.complyt.business.sales_tax.sales_tax_amount;

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
public class SalesTaxCalculationManagerTest {

    @InjectMocks
    SalesTaxCalculationManager salesTaxCalculationManager;

    @Mock
    ItemsSalesTaxCalculator itemsSalesTaxCalculator;

    @Mock
    ShippingFeeSalesTaxCalculator shippingFeeSalesTaxCalculator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    ShippingFee shippingFee;
    List<Item> items;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        shippingFee = createShippingFee();
        items = createItems();
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private List<Item> createItems() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
        return new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    @Test
    void calculate_SalesTaxCalculatedForBothItemsAndShippingFee_SalesTaxAmountReturned() {
        // Given
        float expectedItemsSalesTaxAmount = 1000;
        float expectedShippingFeeSalesTaxAmount = 100;
        float expectedAmount = expectedItemsSalesTaxAmount + expectedShippingFeeSalesTaxAmount;

        // When
        when(itemsSalesTaxCalculator.calculate(items)).thenReturn(expectedItemsSalesTaxAmount);
        when(shippingFeeSalesTaxCalculator.calculate(shippingFee, items)).thenReturn(expectedShippingFeeSalesTaxAmount);

        float actualAmount = salesTaxCalculationManager.calculate(items, shippingFee);

        // Then
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_NullShippingFeePassed_CalculatesSalesTaxAmountOnlyForItems() {
        // Given
        ShippingFee nullShippingFee = null;
        float expectedItemsSalesTaxAmount = 1000;

        // When
        when(itemsSalesTaxCalculator.calculate(items)).thenReturn(expectedItemsSalesTaxAmount);

        float actualAmount = salesTaxCalculationManager.calculate(items, nullShippingFee);

        // Then
        assertEquals(expectedItemsSalesTaxAmount, actualAmount);
    }

    @Test
    void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Item> nullItems = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxCalculationManager.calculate(nullItems,shippingFee);
        });

        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }

}
