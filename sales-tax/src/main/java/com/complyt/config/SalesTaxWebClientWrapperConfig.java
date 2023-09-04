package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SalesTaxWebClientWrapperConfig {

    @Profile({"complytTaxEngine"})
    @Bean("salesTaxWebClientWrapper")
    SalesTaxWebClientWrapper<ComplytSalesTaxRates> complytSalesTaxRatesClientWrapper(@Autowired SalesTaxRatesServiceProxy salesTaxRatesServiceProxy) {
        return new ComplytSalesTaxRatesClientWrapper(salesTaxRatesServiceProxy);
    }

    @Profile({"complytStubTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    SalesTaxWebClientWrapper<ComplytSalesTaxRates> stubComplytSalesTaxRatesClientWrapper() {
        return new StubComplytSalesTaxRatesClientWrapper();
    }

}