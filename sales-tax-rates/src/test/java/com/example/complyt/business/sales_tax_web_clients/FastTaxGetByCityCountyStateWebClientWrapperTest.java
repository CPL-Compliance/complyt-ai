package com.example.complyt.business.sales_tax_web_clients;

import com.complyt.business.sales_tax_web_clients.FastTaxGetByCityCountyStateWebClientWrapper;
import com.complyt.config.web_clients.WebClientWrapperProperties;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FastTaxGetByCityCountyStateWebClientWrapperTest {
    @InjectMocks
    FastTaxGetByCityCountyStateWebClientWrapper fastTaxGetByCityCountyStateWebClientWrapper;
    @InjectMocks
    FastTaxGetByCityCountyStateWebClientWrapper anotherFastTaxGetByCityCountyStateWebClientWrapper;
    @Mock
    WebClient webClient;
    @Mock
    WebClientWrapperProperties fastTaxGetByCityCountyStateWebClientWrapperProperties;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        when(fastTaxGetByCityCountyStateWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(fastTaxGetByCityCountyStateWebClientWrapperProperties.getHost()).thenReturn("host");
        when(fastTaxGetByCityCountyStateWebClientWrapperProperties.getPath()).thenReturn("path");
        when(fastTaxGetByCityCountyStateWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));

        fastTaxGetByCityCountyStateWebClientWrapper = new FastTaxGetByCityCountyStateWebClientWrapper(webClient,
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getScheme(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getHost(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getPath(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getKey());

        anotherFastTaxGetByCityCountyStateWebClientWrapper = new FastTaxGetByCityCountyStateWebClientWrapper(webClient,
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getScheme(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getHost(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getPath(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getKey());
    }

    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(fastTaxGetByCityCountyStateWebClientWrapper.hashCode(), anotherFastTaxGetByCityCountyStateWebClientWrapper.hashCode());
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = new Address("city", "country", "county", "state", "street", "zip", false);
        FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData = new FastTaxGetByCityCountyStateData("city", "county", "countyFips", "state", "", "", "", "", "", "", "", "");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<FastTaxGetByCityCountyStateData>>notNull())).thenReturn(Mono.just(fastTaxGetByCityCountyStateData));

        Mono<SalesTaxData> salesTaxDataMono = fastTaxGetByCityCountyStateWebClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(salesTaxDataMono).expectNext(fastTaxGetByCityCountyStateData).verifyComplete();
    }
}