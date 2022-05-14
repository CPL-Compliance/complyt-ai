package com.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient fastTaxWebClient(WebClient.Builder restTemplateBuilder) {
        return restTemplateBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true))).build();
    }

    @Bean
    public WebClient zipTaxWebClient(WebClient.Builder restTemplateBuilder) {
        return restTemplateBuilder.clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true))).build();
    }
}