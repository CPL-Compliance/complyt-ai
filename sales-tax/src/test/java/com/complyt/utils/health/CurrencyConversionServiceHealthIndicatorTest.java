package com.complyt.utils.health;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyConversionServiceHealthIndicator healthIndicator;

    private final String currencyServiceUrl = "http://mock-service/health";
    private final String currency = "EUR";
    private final String date = "2024-02-26";

    @BeforeEach
    void setUp() {
        healthIndicator = new CurrencyConversionServiceHealthIndicator(restTemplate);
        healthIndicator.currencyServiceUrl = currencyServiceUrl;
        healthIndicator.currency = currency;
        healthIndicator.date = date;
    }

    @Test
    void health_constructor_ShouldInitializeRestTemplate() {
        CurrencyConversionServiceHealthIndicator indicator = new CurrencyConversionServiceHealthIndicator();
        assertNotNull(indicator);
    }

    @Test
    void health_ShouldReturnUp_WhenServiceIsAvailable() {
        String url = currencyServiceUrl + "?currency=" + currency + "&date=" + date;
        ResponseEntity<String> mockResponse = new ResponseEntity<>("OK", HttpStatus.OK);

        when(restTemplate.exchange(url, HttpMethod.GET, null, String.class)).thenReturn(mockResponse);

        Health health = healthIndicator.health();

        assertEquals(Health.up().withDetail("CurrencyConversion", "Available").build(), health);
    }

    @Test
    void health_ShouldReturnDown_WhenServiceReturnsNon2xx() {
        String url = currencyServiceUrl + "?currency=" + currency + "&date=" + date;
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(url, HttpMethod.GET, null, String.class)).thenReturn(mockResponse);

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("CurrencyConversion", "Unexpected Response: " + mockResponse).build(), health);
    }

    @Test
    void health_ShouldReturnDown_WhenTimeoutOccurs() {
        String url = currencyServiceUrl + "?currency=" + currency + "&date=" + date;

        when(restTemplate.exchange(url, HttpMethod.GET, null, String.class)).thenThrow(new ResourceAccessException("Timeout"));

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("CurrencyConversion", "Timeout").build(), health);
    }

    @Test
    void health_ShouldReturnDown_WhenExceptionOccurs() {
        String url = currencyServiceUrl + "?currency=" + currency + "&date=" + date;
        Exception exception = new RuntimeException("Unexpected Error");

        when(restTemplate.exchange(url, HttpMethod.GET, null, String.class)).thenThrow(exception);

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("CurrencyConversion", "Exception: " + exception.getMessage()).build(), health);
    }
}
