package com.complyt.business.sales_tax;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FastTaxWebClientWrapperTest {
    @InjectMocks
    FastTaxWebClientWrapper fastTaxWebClientWrapper;
    FastTaxWebClientWrapper anotherFastTaxWebClientWrapper;

    @Mock
    WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");

        MockitoAnnotations.openMocks(this);
        fastTaxWebClientWrapper = new FastTaxWebClientWrapper(webClient, scheme, host, path, key);

        anotherFastTaxWebClientWrapper = new FastTaxWebClientWrapper(webClient, scheme, host, path, key);
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertTrue(fastTaxWebClientWrapper.equals(anotherFastTaxWebClientWrapper) && anotherFastTaxWebClientWrapper.equals(fastTaxWebClientWrapper));
    }


    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(fastTaxWebClientWrapper.hashCode(), anotherFastTaxWebClientWrapper.hashCode());
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = new Address("city", "country", "county", "state", "street", "zip");
        FastTaxData fastTaxData = new FastTaxData("test", new ArrayList<>());

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<FastTaxData>>notNull())).thenReturn(Mono.just(fastTaxData));

        Mono<SalesTaxData> salesTaxDataMono = fastTaxWebClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(salesTaxDataMono).expectNext(fastTaxData).verifyComplete();
    }
}