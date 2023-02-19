package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShippingFeeSalesTaxRatesProviderTest {

    @InjectMocks
    ShippingFeeSalesTaxRatesProvider shippingFeeSalesTaxRatesProvider;

    @Mock
    SalesTaxRatesProvider salesTaxRatesCalculator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRate salesTaxRate;
    ShippingFee shippingFee;

    ObjectStub objectStub;

    Address address;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules().withSpecialTreatment(true);
        salesTaxRate = objectStub.createSalesTaxRates();
        shippingFee = objectStub.createShippingFee(false, false);
        address = objectStub.createAddress();
    }

    @Test
    void setSalesTaxRates_SetsRatesToShippingFee_ReturnsModifiedShippingFee() {
        // Given
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRate(salesTaxRate);

        // When
        when(salesTaxRatesCalculator.provide(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRate, address)).thenReturn(salesTaxRate);
        ShippingFee actualShippingFee = shippingFeeSalesTaxRatesProvider.setSalesTaxRates(shippingFee, salesTaxRate, address);

        // Then
        assertEquals(shippingFeeWithRates, actualShippingFee);
    }

}
