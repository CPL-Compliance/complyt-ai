package com.complyt.config;

import com.complyt.domain.sales_tax.mappers.FastTaxDataToSalesTaxRateMapper;
import com.complyt.domain.sales_tax.mappers.ZipTaxDataToSalesTaxRateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MappersConfig {

    @Profile({"fastTax", "stubFastTax", "default"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public FastTaxDataToSalesTaxRateMapper fastTaxDataToSalesTaxRateMapper() {
        return FastTaxDataToSalesTaxRateMapper.INSTANCE;
    }

    @Profile({"zipTax"})
    @Bean("salesTaxDataToSalesTaxRateMapper")
    public ZipTaxDataToSalesTaxRateMapper zipTaxDataToSalesTaxRateMapper() {
        return ZipTaxDataToSalesTaxRateMapper.INSTANCE;
    }
}