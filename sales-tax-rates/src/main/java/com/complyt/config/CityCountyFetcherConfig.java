package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import com.complyt.business.data_fetcher.FastTaxGetByCityCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CityCountyFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("getBestMatchCityCountyFetcher")
    public FastTaxGetBestMatchCityCountyFetcher fastTaxGetBestMatchCityCountyAddressFetcher() {
        return new FastTaxGetBestMatchCityCountyFetcher();
    }

    @Profile({"fastTax"})
    @Bean("getByCityCountyFetcher")
    public FastTaxGetByCityCountyFetcher fastTaxGetByCityCountyAddressFetcher() {
        return new FastTaxGetByCityCountyFetcher();
    }

    @Profile({"zipTax"})
    @Bean("cityCountyAddressFetcher")
    public ZipTaxCityCountyFetcher zipTaxAddressFetcher() {
        return new ZipTaxCityCountyFetcher();
    }

    @Profile({"taxJar"})
    @Bean("cityCountyAddressFetcher")
    public TaxJarCityCountyFetcher taxJarCityCountyAddressFetcher() {
        return new TaxJarCityCountyFetcher();
    }
}