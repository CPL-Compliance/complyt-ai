package com.complyt.config;

import com.complyt.business.tax.gt.gt_tax_web_client.GtWebClientWrapper;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.StubComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SalesTaxWebClientWrapperConfigTest {

    @InjectMocks
    SalesTaxWebClientWrapperConfig salesTaxWebClientWrapperConfig;

    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    @Test
    void complytSalesTaxRatesClientWrapper_CreateInstance_ReturnInstance() {
        // Given
        ComplytSalesTaxRatesClientWrapper expectedComplytSalesTaxRatesClientWrapper = new ComplytSalesTaxRatesClientWrapper(salesTaxRatesServiceProxy);
        SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> actualComplytSalesTaxRatesClientWrapper = salesTaxWebClientWrapperConfig.complytSalesTaxRatesClientWrapper(salesTaxRatesServiceProxy);

        // When + Then
        assertEquals(expectedComplytSalesTaxRatesClientWrapper, actualComplytSalesTaxRatesClientWrapper);
    }

    @Test
    void stubComplytSalesTaxRatesClientWrapper_CreateInstance_ReturnInstance() {
        // Given
        StubComplytSalesTaxRatesClientWrapper expectedStubComplytSalesTaxRatesClientWrapper = new StubComplytSalesTaxRatesClientWrapper();
        SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> actualStubComplytSalesTaxRatesClientWrapper = salesTaxWebClientWrapperConfig.stubComplytSalesTaxRatesClientWrapper();

        // When + Then
        assertEquals(expectedStubComplytSalesTaxRatesClientWrapper, actualStubComplytSalesTaxRatesClientWrapper);
    }

    @Test
    void gtWebClientWrapper_CreateInstance_ReturnInstance() {
        // Given
        GtWebClientWrapper expectedGtWebClientWrapper = new GtWebClientWrapper(salesTaxRatesServiceProxy);
        SalesTaxRatesWebClientWrapper<ComplytGtRates> actualGtWebClientWrapper = salesTaxWebClientWrapperConfig.gtWebClientWrapper(salesTaxRatesServiceProxy);

        // When + Then
        assertEquals(expectedGtWebClientWrapper, actualGtWebClientWrapper);
    }

}
