package com.complyt.config;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.FastTaxGetByCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.TaxJarCityCountyStateAddressFetcher;
import com.complyt.business.data_fetcher.ZipTaxCityCountyStateAddressFetcher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@AllArgsConstructor
public class CityCountyStateAddressFetcherConfig {

    @Primary
    @Profile({"fastTax"})
    @Bean("cityCountyStateAddressFetcher")
    public FastTaxGetByCityCountyStateAddressFetcher fastTaxCityCountyStateAddressFetcher() {
        return new FastTaxGetByCityCountyStateAddressFetcher();
    }

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("cityCountyStateAddressFetcher")
    public FastTaxGetBestMatchCityCountyStateAddressFetcher fastTaxAddressFetcher() {
        return new FastTaxGetBestMatchCityCountyStateAddressFetcher();
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