package com.complyt.config.client_wrapper_config;

import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.config.WebClientWrapperProperties;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebhookWebClientWrapperConfig {

    @Bean("webhookClientWrapperProperties")
    public WebhookWebClientWrapper webhookWebClientWrapperProperties(/*@Autowired WebClient webhookWebClient*/WebClient.Builder webClientBuilder,
                                                                     @Autowired WebClientWrapperProperties webhookWebClientWrapperProperties) {
        return new WebhookWebClientWrapper(
                webClientBuilder.clientConnector(
                                new ReactorClientHttpConnector(
                                        HttpClient.create()
                                                .wiretap("reactor.netty.client.HttpClient",
                                                        LogLevel.DEBUG,
                                                        AdvancedByteBufFormat.TEXTUAL)))
                        .build(),
                webhookWebClientWrapperProperties.getScheme(),
                webhookWebClientWrapperProperties.getHost(),
                webhookWebClientWrapperProperties.getPath());
    }

}