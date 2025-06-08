package com.complyt.config.client_wrapper_config;

import com.complyt.business.web_hook.web_clients.WebClientWrapper;
import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.config.WebClientWrapperProperties;
import com.complyt.domain.properties.ComplytIdProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebhookWebClientWrapperConfig<T extends ComplytIdProperty> {

    @Bean("webhookWebClientWrapper")
    public WebClientWrapper<T> webhookWebClientWrapper(@Autowired WebClient webhookWebClient,
                                                       @Autowired WebClientWrapperProperties webhookWebClientWrapperProperties,
                                                       @Value("${hmaac-secret-key}") String secretKey) {
        return new WebhookWebClientWrapper<>(
                webhookWebClient,
                webhookWebClientWrapperProperties.getScheme(),
                webhookWebClientWrapperProperties.getHost(),
                webhookWebClientWrapperProperties.getPath(),
                secretKey);
    }

}