package com.complyt.config;

import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.facade.ExternalSalesTaxRatesFacade;
import com.complyt.facade.InternalSalesTaxRatesFacade;
import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FacadeConfig {

    @Bean("salesTaxRatesFacade")
    @Profile({"stubFastTax", "fastTax", "taxJar", "zipTax"})
    public SalesTaxRatesFacade<ComplytSalesTaxRates> externalSalesTaxRatesFacade(@Autowired AddressValidationService addressValidationService,@Autowired SalesTaxRatesService<ComplytSalesTaxRates> complytSalesTaxRatesService) {
        return new ExternalSalesTaxRatesFacade(addressValidationService, complytSalesTaxRatesService);
    }

    @Bean("salesTaxRatesFacade")
    @Profile({"internalSalesTax","stubInternalRates", "internalRatesSystemTestProfile", "default"})
    public SalesTaxRatesFacade<InternalSalesTaxRates> internalSalesTaxRatesFacade(@Autowired AddressValidationService addressValidationService,
                                                          @Autowired SalesTaxRatesService<InternalSalesTaxRates> internalSalesTaxRatesService,
                                                          @Autowired SalesTaxRatesService<ComplytSalesTaxRates> externalSalesTaxRatesService) {
        return new InternalSalesTaxRatesFacade(addressValidationService, internalSalesTaxRatesService, externalSalesTaxRatesService);
    }
}
