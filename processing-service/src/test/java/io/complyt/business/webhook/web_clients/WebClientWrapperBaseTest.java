package io.complyt.business.webhook.web_clients;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebClientWrapperBaseTest {

    static class TestWrapper extends WebClientWrapperBase {
        public TestWrapper(String scheme, String host, String path) {
            super(null, scheme, host, path);
        }

        public URI callBuildUri(String scheme, String host, String path) {
            return buildUri(scheme, host, path);
        }
    }

    @Test
    void testBuildUri_shouldReturnCorrectUri() {
        String scheme = "https";
        String host = "example.com";
        String path = "/api/test";

        TestWrapper wrapper = new TestWrapper(scheme, host, path);
        URI result = wrapper.callBuildUri(scheme, host, path);

        assertEquals("https://example.com/api/test", result.toString());
    }
}
