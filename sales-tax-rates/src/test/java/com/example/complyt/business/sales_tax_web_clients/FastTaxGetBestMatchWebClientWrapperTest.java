package com.example.complyt.business.sales_tax_web_clients;

import com.complyt.business.sales_tax_web_clients.FastTaxGetBestMatchWebClientWrapper;
import com.complyt.config.web_clients.WebClientWrapperProperties;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FastTaxGetBestMatchWebClientWrapperTest {
    @InjectMocks
    FastTaxGetBestMatchWebClientWrapper fastTaxGetBestMatchWebClientWrapper;

    @InjectMocks
    FastTaxGetBestMatchWebClientWrapper anotherFastTaxGetBestMatchWebClientWrapper;

    @Mock
    WebClient webClient;
    @Mock
    WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        when(fastTaxGetBestMatchWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getHost()).thenReturn("host");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getPath()).thenReturn("path");
        when(fastTaxGetBestMatchWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));

        fastTaxGetBestMatchWebClientWrapper = new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getHost(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());

        anotherFastTaxGetBestMatchWebClientWrapper = new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getHost(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertTrue(fastTaxGetBestMatchWebClientWrapper.equals(anotherFastTaxGetBestMatchWebClientWrapper) && anotherFastTaxGetBestMatchWebClientWrapper.equals(fastTaxGetBestMatchWebClientWrapper));
    }


    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(fastTaxGetBestMatchWebClientWrapper.hashCode(), anotherFastTaxGetBestMatchWebClientWrapper.hashCode());
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = new Address("city", "country", "county", "state", "street", "zip", false);
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("test", new ArrayList<>(), "1");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<FastTaxGetBestMatchData>>notNull())).thenReturn(Mono.just(fastTaxGetBestMatchData));

        Mono<SalesTaxData> salesTaxDataMono = fastTaxGetBestMatchWebClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(salesTaxDataMono).expectNext(fastTaxGetBestMatchData).verifyComplete();
    }
}