package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import com.complyt.business.data_fetcher.FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CityCountyFetcherConfig {

    @Profile({"fastTax"})
    @Bean("getTaxInfoByCityCountyStateCityCountyFetcher")
    public FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher fastTaxGetByCityCountyAddressFetcher() {
        return new FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher();
    }

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("cityCountyFetcher")
    public FastTaxGetBestMatchCityCountyFetcher fastTaxGetBestMatchCityCountyAddressFetcher() {
        return new FastTaxGetBestMatchCityCountyFetcher();
    }

    @Profile({"zipTax"})
    @Bean("cityCountyFetcher")
    public ZipTaxCityCountyFetcher zipTaxAddressFetcher() {
        return new ZipTaxCityCountyFetcher();
    }

    @Profile({"taxJar"})
    @Bean("cityCountyFetcher")
    public TaxJarCityCountyFetcher taxJarCityCountyAddressFetcher() {
        return new TaxJarCityCountyFetcher();
    }
}