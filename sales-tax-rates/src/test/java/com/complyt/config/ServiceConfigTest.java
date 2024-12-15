package com.complyt.config;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.data_fetcher.CityCountyFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import com.complyt.services.ExternalSalesTaxRatesServiceImpl;
import com.complyt.services.InternalSalesTaxRatesServiceImpl;
import com.complyt.services.SalesTaxRatesService;
import com.complyt.services.TaxRateApplicabilityProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ServiceConfigTest {
    @InjectMocks
    ServiceConfig serviceConfig;
    @Mock
    InternalSalesTaxRatesRepository internalSalesTaxRatesRepository;
    @Mock
    ComplytIdHandler complytIdHandler;

    @Mock
    SalesTaxWebClientWrapper getTaxInfoByCityCountyStateWebClientWrapper;
    @Mock
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;
    @Mock
    CityCountyFetcher cityCountyFetcher;
    @Mock
    ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository;
    @Mock
    TaxRateApplicabilityProcessor taxRateApplicabilityProcessor;

    @Test
    void testInternalSalesTaxRatesService() {
        SalesTaxRatesService salesTaxRatesService = serviceConfig.internalSalesTaxRatesService(internalSalesTaxRatesRepository, complytIdHandler, taxRateApplicabilityProcessor);
        assertTrue(salesTaxRatesService instanceof InternalSalesTaxRatesServiceImpl);
    }

    @Test
    void testExternalSalesTaxRatesService() {
        SalesTaxRatesService salesTaxRatesService = serviceConfig.externalSalesTaxRatesService(complytSalesTaxRatesRepository, getTaxInfoByCityCountyStateWebClientWrapper, salesTaxDataToSalesTaxRate, complytIdHandler);
        assertTrue(salesTaxRatesService instanceof ExternalSalesTaxRatesServiceImpl);
    }
}