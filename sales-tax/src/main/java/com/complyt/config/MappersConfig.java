package com.complyt.config;

import com.complyt.domain.sales_tax.mappers.ComplytSalesTaxRatesToSalesTaxRatesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

    @Bean("complytSalesTaxRatesToSalesTaxRatesMapper")
    public ComplytSalesTaxRatesToSalesTaxRatesMapper complytSalesTaxRatesToSalesTaxRatesMapper() {
        return ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE;
    }

}