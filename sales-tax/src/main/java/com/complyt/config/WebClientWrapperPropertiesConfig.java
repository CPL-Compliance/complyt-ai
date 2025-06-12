package com.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class WebClientWrapperPropertiesConfig {

    @Profile({"vowVatValidation"})
    @Bean("vowVatValidationWebClientWrapperProperties")
    public WebClientWrapperProperties vowVatValidationWebClientWrapperProperties() {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ec.europa.eu")
                .path("taxation_customs/vies/rest-api/check-vat-number")
                .build();
    }


    @Profile({"stubVatValidation", "default"})
    @Bean("stubVatValidationWebClientWrapperProperties")
    public WebClientWrapperProperties stubVatValidationWebClientWrapperProperties() {
        return WebClientWrapperProperties.WebClientWrapperPropertiesStub();
    }

}