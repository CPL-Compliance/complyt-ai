package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CountyFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("countyFetcher")
    public FastTaxCountyFetcher FastTaxCountyFetcher(SalesTaxWebClientWrapper salesTaxWebClientWrapper) {
        return new FastTaxCountyFetcher(salesTaxWebClientWrapper);
    }

    @Profile({"zipTax"})
    @Bean("countyFetcher")
    public ZipTaxCountyFetcher ZipTaxCountyFetcher(SalesTaxWebClientWrapper salesTaxWebClientWrapper) {
        return new ZipTaxCountyFetcher(salesTaxWebClientWrapper);
    }
}