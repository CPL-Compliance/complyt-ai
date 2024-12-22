package io.complyt.config.web_clients;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class WebClientWrapperPropertiesConfig {

    @Profile({"here"})
    @Bean("hereWebClientWrapperProperties")
    public WebClientWrapperProperties hereWebClientWrapperProperties(@Value("${here-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("geocode.search.hereapi.com")
                .path("v1/geocode")
                .key(new Pair<>("apiKey", licenseKey)).build();
    }

    @Profile({"default", "stubHere"})
    @Bean("hereWebClientWrapperProperties")
    public WebClientWrapperProperties stubHereWebClientWrapperProperties() {
        return WebClientWrapperProperties.WebClientWrapperPropertiesStub();
    }

    @Profile({"here"})
    @Bean("fastTaxGetBestMatchWebClientWrapperProperties")
    public WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties(@Value("${fast-tax-api-key}") String licenseKey) {
        return WebClientWrapperProperties.builder()
                .scheme("https")
                .host("ws.serviceobjects.com")
                .path("FT/web.svc/json/GetBestMatch")
                .key(new Pair<>("licensekey", licenseKey)).build();
    }
}
