package com.complyt.config;

import com.complyt.annotations.Generated;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
@Generated
public class WebClientsConfig {

    @Profile("complytCurrencyEngine")
    @Bean(name = "complytCurrencyEngineWebClient")
    public WebClient complytCurrencyEngineWebClient(WebClient.Builder webClientBuilder, @Value("${currency-conversion-service-url}") String currencyConversionServiceUrl) {
        return webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .wiretap("reactor.netty.client.HttpClient",
                                                LogLevel.DEBUG,
                                                AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl(currencyConversionServiceUrl)
                .build();
    }

    @Profile({"vowVatValidation", "default"})
    @Bean(name = "vowVatValidationWebClient")
    public WebClient vowVatValidationWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .wiretap("reactor.netty.client.HttpClient",
                                                LogLevel.DEBUG,
                                                AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }

    @Profile({"webhookWebClient", "default"})
    @Bean(name = "webhookWebClient")
    public WebClient webhookWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .wiretap("reactor.netty.client.HttpClient",
                                                LogLevel.DEBUG,
                                                AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }
}

