package com.complyt.config;

import com.complyt.facade.ExternalSalesTaxRatesFacade;
import com.complyt.facade.InternalSalesTaxRatesFacade;
import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FacadeConfigTest {
    @InjectMocks
    FacadeConfig facadeConfig;
    @Mock
    SalesTaxRatesService salesTaxRatesService;
    @Mock
    AddressValidationService addressValidationService;

    @Test
    void testExternalSalesTaxRatesFacade() {
        SalesTaxRatesFacade expectedSalesTaxRatesFacade = new ExternalSalesTaxRatesFacade(addressValidationService, salesTaxRatesService);
        SalesTaxRatesFacade salesTaxRatesFacade = facadeConfig.externalSalesTaxRatesFacade(addressValidationService, salesTaxRatesService);
        assertEquals(expectedSalesTaxRatesFacade, salesTaxRatesFacade);
    }

    @Test
    void testInternalSalesTaxRatesFacade() {
        SalesTaxRatesFacade expectedSalesTaxRatesFacade = new InternalSalesTaxRatesFacade(addressValidationService, salesTaxRatesService, salesTaxRatesService);
        SalesTaxRatesFacade salesTaxRatesFacade = facadeConfig.internalSalesTaxRatesFacade(addressValidationService, salesTaxRatesService, salesTaxRatesService);
        assertEquals(expectedSalesTaxRatesFacade, salesTaxRatesFacade);
    }
}