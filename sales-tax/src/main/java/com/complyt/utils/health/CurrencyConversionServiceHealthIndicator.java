package com.complyt.utils.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("complytCurrencyEngine")
public class CurrencyConversionServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    @Value("${currencyClient.health.url}")
    String currencyServiceUrl;

    @Value("${currencyClient.health.currency}")
    String currency;

    @Value("${currencyClient.health.date}")
    String date;

    public CurrencyConversionServiceHealthIndicator() {
        this.restTemplate = new RestTemplate();
    }

    public CurrencyConversionServiceHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {

        try {
            String url = currencyServiceUrl + "?currency=" + currency + "&date=" + date;
            ResponseEntity<String> currencyResponse = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (currencyResponse.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("CurrencyConversion", "Available").build();
            } else {
                return Health.down().withDetail("CurrencyConversion", "Unexpected Response: " + currencyResponse).build();
            }
        } catch (ResourceAccessException e) {
            return Health.down().withDetail("CurrencyConversion", "Timeout").build();
        } catch (Exception e) {
            return Health.down().withDetail("CurrencyConversion", "Exception: " + e.getMessage()).build();
        }
    }
}