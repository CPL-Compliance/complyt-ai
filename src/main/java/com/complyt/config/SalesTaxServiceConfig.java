package com.complyt.config;

import com.complyt.services.FastTaxService;
import com.complyt.services.ZipTaxService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SalesTaxServiceConfig {

    @Profile({"zipTax", "default"})
    @Bean("salesTaxService")
    public ZipTaxService zipTaxService(RestTemplate zipTaxRestTemplate) {
        return new ZipTaxService(zipTaxRestTemplate);
    }

    @Profile("fastTax")
    @Bean("salesTaxService")
    public FastTaxService fastTaxService(RestTemplate fastTaxRestTemplate) {
        return new FastTaxService(fastTaxRestTemplate);
    }
}
