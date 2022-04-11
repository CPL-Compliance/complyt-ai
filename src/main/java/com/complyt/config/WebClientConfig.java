package com.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient fastTaxWebClient(WebClient.Builder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public WebClient zipTaxWebClient(WebClient.Builder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
