package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingFee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingFeeSalesTaxRatesProviderTest {

    @InjectMocks
    ShippingFeeSalesTaxRatesProvider shippingFeeSalesTaxRatesProvider;

    @Mock
    SalesTaxRatesProvider salesTaxRatesCalculator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRates salesTaxRates;
    ShippingFee shippingFee;

    UnitTestUtilities testUtilities;

    Address address;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules().withSpecialTreatment(true);
        salesTaxRates = testUtilities.createSalesTaxRates();
        shippingFee = testUtilities.createShippingFee(false, false);
        address = testUtilities.createAddress();
    }

    @Test
    void setSalesTaxRates_SetsRatesToShippingFee_ReturnsModifiedShippingFee() {
        // Given
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRates(salesTaxRates);

        // When
        when(salesTaxRatesCalculator.provide(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRates, address)).thenReturn(salesTaxRates);
        ShippingFee actualShippingFee = shippingFeeSalesTaxRatesProvider.setSalesTaxRates(shippingFee, salesTaxRates, address);

        // Then
        assertEquals(shippingFeeWithRates, actualShippingFee);
    }

}
