package com.complyt.config.web_clients;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class WebClientWrapperPropertiesConfig {


    @Primary
    @Profile("fastTax")
    @Bean("fastTaxGetByCityCountyStateWebClientWrapperProperties")
    public WebClientWrapperProperties fastTaxGetByCityCountyStateWebClientWrapperProperties(@Value("${fast-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ws.serviceobjects.com")
                .path("FT/web.svc/json/GetTaxInfoByCityCountyState")
                .key(new Pair<>("licensekey", licenseKey)).build();
    }

    @Profile("fastTax")
    @Bean("fastTaxGetBestMatchWebClientWrapperProperties")
    public WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties(@Value("${fast-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ws.serviceobjects.com")
                .path("FT/web.svc/json/GetBestMatch")
                .key(new Pair<>("licensekey", licenseKey)).build();
    }

    @Profile("zipTax")
    @Bean("zipTaxWebClientWrapperProperties")
    public WebClientWrapperProperties zipTaxWebClientWrapperProperties(@Value("${zip-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("api.zip-tax.com")
                .path("request/v40")
                .key(new Pair<>("key", licenseKey)).build();
    }

    @Profile({"taxJar"})
    @Bean("taxJarWebClientWrapperProperties")
    public WebClientWrapperProperties taxJarWebClientWrapperProperties() {
        return WebClientWrapperProperties.WebClientWrapperPropertiesStub();
    }

    @Profile({"stubFastTax", "default"})
    @Bean("stubFastTaxWebClientWrapperProperties")
    public WebClientWrapperProperties stubFastTaxWebClientWrapperProperties() {
        return WebClientWrapperProperties.WebClientWrapperPropertiesStub();
    }

}
