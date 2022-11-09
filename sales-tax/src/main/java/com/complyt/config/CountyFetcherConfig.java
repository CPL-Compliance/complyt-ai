package com.complyt.config;

import com.complyt.business.data_fetcher.TransactionFastTaxCountyFetcher;
import com.complyt.business.data_fetcher.TransactionZipTaxCountyFetcher;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CountyFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("countyFetcher")
    public TransactionFastTaxCountyFetcher transactionFastTaxCountyFetcher(SalesTaxWebClientWrapper salesTaxWebClientWrapper) {
        return new TransactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
    }

    @Profile({"zipTax"})
    @Bean("countyFetcher")
    public TransactionZipTaxCountyFetcher transactionZipTaxCountyFetcher(SalesTaxWebClientWrapper salesTaxWebClientWrapper) {
        return new TransactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
    }
}