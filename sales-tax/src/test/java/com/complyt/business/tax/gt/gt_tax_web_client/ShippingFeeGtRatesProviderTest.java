package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.GtRatesProvider;
import com.complyt.business.tax.gt.ShippingFeeGtRatesProvider;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingFeeGtRatesProviderTest {

    @InjectMocks
    ShippingFeeGtRatesProvider shippingFeeGstRatesProvider;
    @Mock
    GtRatesProvider gtRatesProvider;
    UnitTestUtilities testUtilities;
    ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        shippingFee = testUtilities.createShippingFee(false, true, true);
    }

    @Test
    void setGstRates_SetsGstRatesInToItems_ReturnsItems() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        GtAddress gtAddress = testUtilities.createCanadaGtAddress();
        ShippingFee expectedShippingFee = shippingFee.withGtRates(gtRates);

        // When
        when(gtRatesProvider.provide(shippingFee.getJurisdictionalTaxRules(), gtRates, gtAddress)).thenReturn(gtRates);
        ShippingFee actualShippingFee = shippingFeeGstRatesProvider.setGtRates(shippingFee, gtRates, gtAddress);

        // Then
        Assertions.assertEquals(expectedShippingFee, actualShippingFee);
    }

}