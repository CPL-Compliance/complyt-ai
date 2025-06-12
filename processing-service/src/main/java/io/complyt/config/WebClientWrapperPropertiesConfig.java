package io.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientWrapperPropertiesConfig {

    @Bean("webhookWebClientWrapperProperties")
    public WebClientWrapperProperties webhookWebClientWrapperProperties() {
        return WebClientWrapperProperties
                .builder()
                .scheme("https")
                .host("")
                .path("")
                .build();
    }
}
