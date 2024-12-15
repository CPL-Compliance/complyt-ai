package io.complyt.config.web_clients;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebClientWrapperPropertiesConfigTest {
    WebClientWrapperPropertiesConfig webClientWrapperPropertiesConfig;

    @BeforeEach
    void setUp() {
        webClientWrapperPropertiesConfig = new WebClientWrapperPropertiesConfig();
    }

    @Test
    void hereWebClientWrapperProperties_createHereWebClientWrapperProperties_getHereWebClientWrapperProperties() {
        String licenseKey = "License Key";
        WebClientWrapperProperties expectedHereWebClientWrapperProperties = WebClientWrapperProperties.builder()
                .scheme("https")
                .host("geocode.search.hereapi.com")
                .path("v1/geocode")
                .key(new Pair<>("apiKey", licenseKey)).build();

        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.hereWebClientWrapperProperties(licenseKey);

        assertEquals(expectedHereWebClientWrapperProperties, actualFastTaxWebClientWrapper);
    }
}