package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShippingFeeSalesTaxRatesCalculatorTest {

    @InjectMocks
    ShippingFeeSalesTaxRatesCalculator shippingFeeSalesTaxRatesCalculator;

    @Mock
    SalesTaxRatesCalculator salesTaxRatesCalculator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRate salesTaxRate;
    ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        salesTaxRate = createSalesTaxRates();
        shippingFee = createShippingFee();
    }

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private ShippingFee createShippingFee() {
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules, null, "C6S1");
    }

    @Test
    void setSalesTaxRates_SetsRatesToShippingFee_ReturnsModifiedShippingFee() {
        // Given
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRate(salesTaxRate);

        // When
        when(salesTaxRatesCalculator.calculateSalesTaxRate(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRate)).thenReturn(salesTaxRate);
        ShippingFee actualShippingFee = shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(shippingFee, salesTaxRate);

        // Then
        assertEquals(shippingFeeWithRates, actualShippingFee);
    }

}
