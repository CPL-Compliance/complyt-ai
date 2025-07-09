package io.complyt.config;

import io.complyt.business.webhook.web_clients.WebClientWrapper;
import io.complyt.business.webhook.web_clients.WebhookWebClientWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class WebhookWebClientWrapperConfigTest {

    @Mock
    private WebClient mockWebClient;

    private WebClientWrapperProperties mockProperties;
    private final String secretKey = "test-secret";

    private WebhookWebClientWrapperConfig<TestIdProperty> config;

    static class TestIdProperty implements ComplytIdProperty {
        public String getComplytId() {
            return "dummy-id";
        }
    }

    @BeforeEach
    void setup() {
        mockProperties = WebClientWrapperProperties.builder()
                .scheme("https")
                .host("api.test.com")
                .path("/webhook")
                .build();

        config = new WebhookWebClientWrapperConfig<>();
    }

    @Test
    void testWebhookWebClientWrapperBeanCreation() {
        WebClientWrapper<TestIdProperty> wrapper = config.webhookWebClientWrapper(mockWebClient, mockProperties, secretKey);

        assertThat(wrapper).isNotNull();
        assertThat(wrapper).isInstanceOf(WebhookWebClientWrapper.class);
    }

}