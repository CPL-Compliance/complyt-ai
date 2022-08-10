package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.utils.data_fetcher.TransactionFastTaxCountyFetcher;
import com.complyt.business.utils.data_fetcher.TransactionZipTaxCountyFetcher;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class CountyFetcherConfig {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Profile({"fastTax", "default"})
    @Bean("countyFetcher")
    public TransactionFastTaxCountyFetcher transactionFastTaxCountyFetcher(WebClient webClient) {
        return new TransactionFastTaxCountyFetcher(salesTaxWebClientWrapper);
    }

    @Profile({"zipTax", "default"})
    @Bean("countyFetcher")
    public TransactionZipTaxCountyFetcher transactionZipTaxCountyFetcher(WebClient webClient) {
        return new TransactionZipTaxCountyFetcher(salesTaxWebClientWrapper);
    }
}
