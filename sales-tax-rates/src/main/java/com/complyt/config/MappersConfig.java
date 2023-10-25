package com.complyt.config;

import com.complyt.domain.mappers.FastTaxGetBestMatchDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.FastTaxGetByCityCountyStateDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.TaxJarDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.ZipTaxDataToSalesTaxRateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class MappersConfig {

    @Primary
    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public FastTaxGetBestMatchDataToSalesTaxRateMapper fastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapper() {
        return FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"fastTax"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public FastTaxGetByCityCountyStateDataToSalesTaxRateMapper fastTaxGetByCityCountyDataToSalesTaxRateMapper() {
        return FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"zipTax"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public ZipTaxDataToSalesTaxRateMapper zipTaxDataToSalesTaxRateMapper() {
        return ZipTaxDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"taxJar"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public TaxJarDataToSalesTaxRateMapper taxJarDataToSalesTaxRateMapper() {
        return TaxJarDataToSalesTaxRateMapper.INSTANCE;
    }
}