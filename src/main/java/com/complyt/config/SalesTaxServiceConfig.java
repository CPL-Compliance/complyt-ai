package com.complyt.config;

import com.complyt.services.FastTaxService;
import com.complyt.services.ZipTaxService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SalesTaxServiceConfig {

    @Bean
    public ZipTaxService zipTaxService(RestTemplate zipTaxRestTemplate) {
        return new ZipTaxService(zipTaxRestTemplate);
    }

    @Bean
    public FastTaxService fastTaxService(RestTemplate zipTaxRestTemplate) {
        return new FastTaxService(zipTaxRestTemplate);
    }
}
