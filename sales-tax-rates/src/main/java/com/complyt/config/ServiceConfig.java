package com.complyt.config;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import com.complyt.services.ExternalSalesTaxRatesServiceImpl;
import com.complyt.services.InternalSalesTaxRatesServiceImpl;
import com.complyt.services.SalesTaxRatesService;
import com.complyt.services.TaxRateApplicabilityProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ServiceConfig {

    @Bean("internalSalesTaxRatesService")
    @Profile({"internalSalesTax", "stubInternalRates", "internalRatesSystemTestProfile",})
    public SalesTaxRatesService<InternalSalesTaxRates> internalSalesTaxRatesService(@Autowired InternalSalesTaxRatesRepository internalSalesTaxRatesRepository,
                                                                                    @Autowired ComplytIdHandler<InternalSalesTaxRates> complytIdHandler, @Autowired TaxRateApplicabilityProcessor taxRateApplicabilityProcessor) {
        return new InternalSalesTaxRatesServiceImpl(internalSalesTaxRatesRepository, complytIdHandler, taxRateApplicabilityProcessor);
    }

    @Bean("externalSalesTaxRatesService")
    @Profile({"internalSalesTax", "stubFastTax", "stubInternalRates", "internalRatesSystemTestProfile", "fastTax", "taxJar", "zipTax", "default"})
    public SalesTaxRatesService<ComplytSalesTaxRates> externalSalesTaxRatesService(@Autowired ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository,
                                                                                   @Autowired SalesTaxWebClientWrapper salesTaxWebClientWrapper,
                                                                                   @Autowired SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate,
                                                                                   @Autowired ComplytIdHandler<ComplytSalesTaxRates> complytIdHandler) {
        return new ExternalSalesTaxRatesServiceImpl<>(complytSalesTaxRatesRepository, salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, complytIdHandler);
    }
}
