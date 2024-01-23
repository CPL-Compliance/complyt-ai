package io.complyt.authentication.config;

import io.complyt.authentication.annotations.Generated;
import io.netty.handler.logging.LogLevel;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Generated
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder,
                               @NonNull @Value("${authorization.authorization-server-base-url}") String serverUrl) {
        return webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .wiretap("reactor.netty.client.HttpClient",
                                                LogLevel.DEBUG,
                                                AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl(serverUrl)
                .build();
    }
}