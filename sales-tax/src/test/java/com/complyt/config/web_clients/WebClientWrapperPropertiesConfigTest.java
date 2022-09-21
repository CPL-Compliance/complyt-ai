package com.complyt.config.web_clients;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebClientWrapperPropertiesConfigTest {
    WebClientWrapperPropertiesConfig webClientWrapperPropertiesConfig;

    @BeforeEach
    void setUp() {
        webClientWrapperPropertiesConfig = new WebClientWrapperPropertiesConfig();
    }

    @Test
    void fastTaxWebClientWrapper() {
        String licenseKey = "License Key";
        WebClientWrapperProperties expectedFastTaxWebClientWrapper = new WebClientWrapperProperties("https",
                "ws.serviceobjects.com",
                "FT/web.svc/json/GetBestMatch",
                new Pair<>("licensekey", licenseKey));

        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.fastTaxWebClientWrapper(licenseKey);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }

    @Test
    void zipTaxWebClientWrapper() {
        String licenseKey = "License Key";
        WebClientWrapperProperties expectedFastTaxWebClientWrapper = new WebClientWrapperProperties("https",
                "api.zip-tax.com",
                "request/v40",
                new Pair<>("key", licenseKey));

        WebClientWrapperProperties actualFastTaxWebClientWrapper = webClientWrapperPropertiesConfig.zipTaxWebClientWrapper(licenseKey);

        assertEquals(expectedFastTaxWebClientWrapper, actualFastTaxWebClientWrapper);
    }
}