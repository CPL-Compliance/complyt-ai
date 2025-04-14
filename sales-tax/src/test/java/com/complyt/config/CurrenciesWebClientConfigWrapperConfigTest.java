package com.complyt.config;

import com.complyt.business.strategy.currencyExchange.ComplytCurrenciesWebClientWrapper;
import com.complyt.business.strategy.currencyExchange.CurrenciesWebClientWrapper;
import com.complyt.business.strategy.currencyExchange.StubCurrenciesWebClientWrapper;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class CurrenciesWebClientConfigWrapperConfigTest {

    CurrenciesWebClientWrapperConfig currenciesWebClientWrapperConfig;

    WebClient webClient;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        currenciesWebClientWrapperConfig = new CurrenciesWebClientWrapperConfig();
        webClient = WebClient.builder().build();
    }

    @Test
    public void currenciesWebClientWrapper_createComplytCurrenciesWebClientWrapper_getComplytCurrenciesWebClientWrapper() {
        CurrenciesWebClientWrapper expectedComplytCurrenciesWebClientWrapper = new ComplytCurrenciesWebClientWrapper(webClient);

        CurrenciesWebClientWrapper actualComplytCurrenciesWebClientWrapper = currenciesWebClientWrapperConfig.currenciesWebClientWrapper(webClient);

        assertEquals(expectedComplytCurrenciesWebClientWrapper, actualComplytCurrenciesWebClientWrapper);
    }

    @Test
    public void currenciesWebClientWrapper_createStubCurrenciesWebClientWrapper_getStubCurrenciesWebClientWrapper() {
        CurrenciesWebClientWrapper expectedStubCurrenciesWebClientWrapper = new StubCurrenciesWebClientWrapper();

        CurrenciesWebClientWrapper actualStubCurrenciesWebClientWrapper = currenciesWebClientWrapperConfig.stubCurrenciesWebClientWrapper();

        assertEquals(expectedStubCurrenciesWebClientWrapper, actualStubCurrenciesWebClientWrapper);
    }
}
