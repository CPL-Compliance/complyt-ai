package io.complyt.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class WebClientWrapperPropertiesConfigTest {

    private final WebClientWrapperPropertiesConfig config = new WebClientWrapperPropertiesConfig();

    @Test
    void testWebhookWebClientWrapperPropertiesBean() {
        WebClientWrapperProperties props = config.webhookWebClientWrapperProperties();

        assertThat(props).isNotNull();
        assertThat(props.getScheme()).isEqualTo("https");
        assertThat(props.getHost()).isEmpty();
        assertThat(props.getPath()).isEmpty();
    }
}
