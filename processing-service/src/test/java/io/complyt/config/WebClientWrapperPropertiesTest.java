package io.complyt.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientWrapperPropertiesTest {

    @Test
    void testBuilderCreatesCorrectInstance() {
        WebClientWrapperProperties props = WebClientWrapperProperties.builder()
                .scheme("https")
                .host("api.example.com")
                .path("/endpoint")
                .build();

        assertThat(props.getScheme()).isEqualTo("https");
        assertThat(props.getHost()).isEqualTo("api.example.com");
        assertThat(props.getPath()).isEqualTo("/endpoint");
    }

    @Test
    void testWebClientWrapperPropertiesStub() {
        WebClientWrapperProperties stub = WebClientWrapperProperties.WebClientWrapperPropertiesStub();

        assertThat(stub.getScheme()).isEmpty();
        assertThat(stub.getHost()).isEmpty();
        assertThat(stub.getPath()).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        WebClientWrapperProperties one = WebClientWrapperProperties.builder()
                .scheme("http")
                .host("localhost")
                .path("/test")
                .build();

        WebClientWrapperProperties two = WebClientWrapperProperties.builder()
                .scheme("http")
                .host("localhost")
                .path("/test")
                .build();

        assertThat(one).isEqualTo(two);
        assertThat(one.hashCode()).isEqualTo(two.hashCode());
    }

    @Test
    void testNotEquals() {
        WebClientWrapperProperties one = WebClientWrapperProperties.builder()
                .scheme("http")
                .host("localhost")
                .path("/a")
                .build();

        WebClientWrapperProperties two = WebClientWrapperProperties.builder()
                .scheme("http")
                .host("localhost")
                .path("/b")
                .build();

        assertThat(one).isNotEqualTo(two);
    }
}
