package io.complyt.config;

import io.complyt.business.webhook.web_clients.WebClientWrapper;
import io.complyt.business.webhook.web_clients.WebhookWebClientWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebhookWebClientWrapperConfig<T extends ComplytIdProperty> {

    @Bean("WebhookWebClientWrapper")
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