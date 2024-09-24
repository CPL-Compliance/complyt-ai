package com.complyt.business.strategy.currencyexchange;


import com.complyt.business.strategy.currencyExchange.ComplytCurrenciesWebClientWrapper;
import com.complyt.domain.currency.CurrencyExchangeRateObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ComplytCurrenciesWebClientConfigWrapperTest {

    @InjectMocks
    ComplytCurrenciesWebClientWrapper complytCurrenciesWebClientWrapper;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Captor
    private ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor;

    @Mock
    WebClient webClient;

    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
    }

    @Test
    void getExchangeRateByCurrencyAndDate_validRequestWithCurrencyAndDate_SuccessReturnsCurrencyExchangeRateObject() {
        // Given
        String currency = "USD";
        LocalDateTime date = LocalDateTime.now();
        CurrencyExchangeRateObject currencyExchangeRateResponse = unitTestUtilities.createCurrencyExchangeRateObject(currency, date, BigDecimal.ONE);

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(CurrencyExchangeRateObject.class)).thenReturn(Mono.just(currencyExchangeRateResponse));

        Mono<CurrencyExchangeRateObject> currencyExchangeRateObjectMono = complytCurrenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(currency, date);

        // Then
        StepVerifier.create(currencyExchangeRateObjectMono)
                .expectNext(currencyExchangeRateResponse)
                .verifyComplete();
    }

    @Test
    void getExchangeRateByCurrencyAndDate_retriesFailed_FailureThrowsRunTimeException() {
        // Given
        String currency = "USD";
        LocalDateTime date = LocalDateTime.now();

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(CurrencyExchangeRateObject.class))
                .thenReturn(Mono.error(new RuntimeException(" Retries Exhausted")));

        Mono<CurrencyExchangeRateObject> currencyExchangeRateObjectMono = complytCurrenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(currency, date);

        // Then
        StepVerifier.create(currencyExchangeRateObjectMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains(" Retries Exhausted"))
                .verify();
    }

    @Test
    void getExchangeRateByCurrencyAndDate_validRequestChecksSentCurrencyAndDate_SuccessReturnsCurrencyExchangeRateObject() {
        // Given
        String currency = "USD";
        LocalDateTime date = LocalDateTime.now();
        CurrencyExchangeRateObject currencyExchangeRateResponse = unitTestUtilities.createCurrencyExchangeRateObject(currency, date, BigDecimal.ONE);

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(CurrencyExchangeRateObject.class)).thenReturn(Mono.just(currencyExchangeRateResponse));

        Mono<CurrencyExchangeRateObject> currencyExchangeRateObjectMono = complytCurrenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(currency, date);

        // Then
        StepVerifier.create(currencyExchangeRateObjectMono)
                .expectNext(currencyExchangeRateResponse)
                .verifyComplete();

        // Verify that the URI function sets the correct query parameters
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriBuilderMock.queryParam(anyString(), Optional.ofNullable(any()))).thenReturn(uriBuilderMock);
        uriFunctionCaptor.getValue().apply(uriBuilderMock);

        verify(uriBuilderMock).queryParam("currency", currency);
        verify(uriBuilderMock).queryParam("date", date);
    }
}