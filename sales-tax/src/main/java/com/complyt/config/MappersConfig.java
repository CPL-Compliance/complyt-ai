package com.complyt.config;

import com.complyt.domain.sales_tax.mappers.ComplytSalesTaxRatesToSalesTaxRatesMapper;
import com.complyt.domain.sales_tax.mappers.FastTaxDataToSalesTaxRateMapper;
import com.complyt.domain.sales_tax.mappers.ZipTaxDataToSalesTaxRateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MappersConfig {

    @Bean("complytSalesTaxRatesToSalesTaxRatesMapper")
    public ComplytSalesTaxRatesToSalesTaxRatesMapper complytSalesTaxRatesToSalesTaxRatesMapper() {
        return ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE;
    }

}