package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CountyFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("countyFetcher")
    public FastTaxCountyFetcher fastTaxAddressFetcher() {
        return new FastTaxCountyFetcher();
    }

    @Profile({"zipTax"})
    @Bean("countyFetcher")
    public ZipTaxCountyFetcher zipTaxAddressFetcher() {
        return new ZipTaxCountyFetcher();
    }

    @Profile({"taxJar"})
    @Bean("countyFetcher")
    public TaxJarCountyFetcher taxJarCountyFetcher() {
        return new TaxJarCountyFetcher();
    }
}