package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.FastTaxGetByCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyStateAddressFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CityCountyStateAddressFetcherConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("getBestMatchCityCountyStateAddressFetcher")
    public FastTaxGetBestMatchCityCountyStateAddressFetcher fastTaxGetBestMatchCityCountyStateAddressFetcher() {
        return new FastTaxGetBestMatchCityCountyStateAddressFetcher();
    }

    @Profile({"fastTax"})
    @Bean("getByCityCountyStateFetcher")
    public FastTaxGetByCityCountyStateAddressFetcher fastTaxGetByCityCountyStateAddressFetcher() {
        return new FastTaxGetByCityCountyStateAddressFetcher();
    }

    @Profile({"zipTax"})
    @Bean("cityCountyStateAddressFetcher")
    public ZipTaxCityCountyStateAddressFetcher zipTaxAddressFetcher() {
        return new ZipTaxCityCountyStateAddressFetcher();
    }

    @Profile({"taxJar"})
    @Bean("cityCountyStateAddressFetcher")
    public TaxJarCityCountyStateAddressFetcher taxJarCityCountyStateAddressFetcher() {
        return new TaxJarCityCountyStateAddressFetcher();
    }
}