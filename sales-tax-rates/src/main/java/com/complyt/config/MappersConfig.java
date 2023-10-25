package com.complyt.config;

import com.complyt.domain.mappers.FastTaxGetBestMatchDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.FastTaxGetByCityCountyStateDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.TaxJarDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.ZipTaxDataToSalesTaxRateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MappersConfig {

    @Profile({"fastTax"})
    @Bean("getByCityCountyDataToSalesTaxRateMapper")
    public FastTaxGetByCityCountyStateDataToSalesTaxRateMapper fastTaxGetByCityCountyDataToSalesTaxRateMapper() {
        return FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("getBestMatchDataToSalesTaxRateMapper")
    public FastTaxGetBestMatchDataToSalesTaxRateMapper fastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapper() {
        return FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE;
    }


    @Profile({"zipTax"})
    @Bean("getBestMatchDataToSalesTaxRateMapper")
    public ZipTaxDataToSalesTaxRateMapper zipTaxDataToSalesTaxRateMapper() {
        return ZipTaxDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"taxJar"})
    @Bean("getBestMatchDataToSalesTaxRateMapper")
    public TaxJarDataToSalesTaxRateMapper taxJarDataToSalesTaxRateMapper() {
        return TaxJarDataToSalesTaxRateMapper.INSTANCE;
    }
}