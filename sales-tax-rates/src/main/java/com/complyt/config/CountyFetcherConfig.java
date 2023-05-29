package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxAddressFetcher;
import com.complyt.business.data_fetcher.ZipTaxAddressFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CountyFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("countyFetcher")
    public FastTaxAddressFetcher FastTaxCountyFetcher() {
        return new FastTaxAddressFetcher();
    }

    @Profile({"zipTax"})
    @Bean("countyFetcher")
    public ZipTaxAddressFetcher ZipTaxCountyFetcher() {
        return new ZipTaxAddressFetcher();
    }
}