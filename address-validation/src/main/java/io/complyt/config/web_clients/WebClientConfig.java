package io.complyt.config.web_clients;


import io.complyt.annotations.Generated;
import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Generated
@Configuration
public class WebClientConfig {

    @Profile({"here", "default", "stubHere"})
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .wiretap("reactor.netty.client.HttpClient",
                                                LogLevel.DEBUG,
                                                AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }
}