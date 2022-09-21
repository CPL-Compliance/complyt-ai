package com.complyt.config.web_clients;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientWrapperPropertiesConfig {

    @Profile({"fastTax", "default"})
    @Bean("fastTaxWebClientWrapperProperties")
    public WebClientWrapperProperties fastTaxWebClientWrapper(@Value("${fast-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ws.serviceobjects.com")
                .path("FT/web.svc/json/GetBestMatch")
                .key(new Pair<>("licensekey", licenseKey)).build();
    }

    @Profile("zipTax")
    @Bean("zipTaxWebClientWrapperProperties")
    public WebClientWrapperProperties zipTaxWebClientWrapper(@Value("${zip-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("api.zip-tax.com")
                .path("request/v40")
                .key(new Pair<>("key", licenseKey)).build();
    }
}
