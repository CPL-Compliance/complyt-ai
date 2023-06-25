package com.example.complyt.config.web_clients;

import com.complyt.config.web_clients.WebClientWrapperProperties;
import com.complyt.config.web_clients.WebClientWrapperPropertiesConfig;
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
    void fastTaxWebClientWrapperProperties_createFastTaxWebClientWrapperProperties_getFastTaxWebClientWrapperProperties() {
        String licenseKey = "License Key";
        WebClientWrapperProperties expectedFastTaxWebClientWrapper = new WebClientWrapperProperties("https",
                "ws.serviceobjects.com",
                "FT/web.svc/json/GetBestMatch",
                new Pair<>("licensekey", licenseKey));

        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.fastTaxWebClientWrapperProperties(licenseKey);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }

    @Test
    void zipTaxWebClientWrapperProperties_createZipTaxWebClientWrapperProperties_getZipTaxWebClientWrapperProperties() {
        String licenseKey = "License Key";
        WebClientWrapperProperties expectedFastTaxWebClientWrapper = new WebClientWrapperProperties("https",
                "api.zip-tax.com",
                "request/v40",
                new Pair<>("key", licenseKey));

        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.zipTaxWebClientWrapperProperties(licenseKey);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }

    @Test
    void taxJarWebClientWrapperProperties_createTaxJarWebClientWrapperProperties_getTaxJarWebClientWrapperProperties() {
        // Given
        WebClientWrapperProperties expectedTaxJarWebClientWrapper = WebClientWrapperProperties.WebClientWrapperPropertiesStub();

        // When
        WebClientWrapperProperties actualTaxJarWebClientWrapper = webClientWrapperPropertiesConfig.taxJarWebClientWrapperProperties();

        // Then
        assertEquals(expectedTaxJarWebClientWrapper, actualTaxJarWebClientWrapper);
    }

    @Test
    void stubFastTaxWebClientWrapperProperties_createStubFastTaxWebClientWrapperProperties_getStubFastTaxWebClientWrapperProperties() {
        // Given
        WebClientWrapperProperties expectedFastTaxWebClientWrapper = WebClientWrapperProperties.WebClientWrapperPropertiesStub();

        // When
        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.stubFastTaxWebClientWrapperProperties();

        // Then
        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }
}