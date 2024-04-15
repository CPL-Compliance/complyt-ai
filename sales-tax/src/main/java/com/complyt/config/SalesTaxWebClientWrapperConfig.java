package com.complyt.config;

import com.complyt.business.tax.gt.gt_tax_web_client.GtWebClientWrapper;
import com.complyt.business.tax.gt.gt_tax_web_client.StubGtWebClientWrapper;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.StubComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SalesTaxWebClientWrapperConfig {

    @Profile({"complytTaxEngine"})
    @Bean("salesTaxWebClientWrapper")
    public SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> complytSalesTaxRatesClientWrapper(@Autowired SalesTaxRatesServiceProxy salesTaxRatesServiceProxy) {
        return new ComplytSalesTaxRatesClientWrapper(salesTaxRatesServiceProxy);
    }

    @Profile({"complytStubTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    public SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> stubComplytSalesTaxRatesClientWrapper() {
        return new StubComplytSalesTaxRatesClientWrapper();
    }

    @Profile({"complytTaxEngine"})
    @Bean("gtWebClientWrapper")
    public SalesTaxRatesWebClientWrapper<ComplytGtRates> gtWebClientWrapper(@Autowired SalesTaxRatesServiceProxy salesTaxRatesServiceProxy) {
        return new GtWebClientWrapper(salesTaxRatesServiceProxy);
    }

    @Profile({"complytStubTax", "default"})
    @Bean("gtWebClientWrapper")
    public SalesTaxRatesWebClientWrapper<ComplytGtRates> stubGtWebClientWrapper() {
        return new StubGtWebClientWrapper();
    }

}