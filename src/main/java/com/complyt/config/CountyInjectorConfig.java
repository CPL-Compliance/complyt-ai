package com.complyt.config;

import com.complyt.business.utils.transaction_data_injector.FastTaxCountyInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class CountyInjectorConfig {

    @Profile({"fastTax", "default"})
    @Bean("countyInjector")
    public FastTaxCountyInjector fastTaxCountyInjector() {
        return new FastTaxCountyInjector();
    }
}
