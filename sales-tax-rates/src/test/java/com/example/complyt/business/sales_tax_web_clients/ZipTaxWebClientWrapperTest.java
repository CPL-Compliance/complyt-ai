package com.example.complyt.business.sales_tax_web_clients;

import com.complyt.business.sales_tax_web_clients.ZipTaxWebClientWrapper;
import com.complyt.config.web_clients.WebClientWrapperProperties;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class ZipTaxWebClientWrapperTest {
    @InjectMocks
    ZipTaxWebClientWrapper zipTaxWebClientWrapper;

    @InjectMocks
    ZipTaxWebClientWrapper anotherZipTaxWebClientWrapper;

    @Mock
    WebClient webClient;
    @Mock
    WebClientWrapperProperties zipTaxWebClientWrapperProperties;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        when(zipTaxWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(zipTaxWebClientWrapperProperties.getHost()).thenReturn("host");
        when(zipTaxWebClientWrapperProperties.getPath()).thenReturn("path");
        when(zipTaxWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));

        zipTaxWebClientWrapper = new ZipTaxWebClientWrapper(webClient,
                zipTaxWebClientWrapperProperties.getScheme(),
                zipTaxWebClientWrapperProperties.getHost(),
                zipTaxWebClientWrapperProperties.getPath(),
                zipTaxWebClientWrapperProperties.getKey());

        anotherZipTaxWebClientWrapper = new ZipTaxWebClientWrapper(webClient,
                zipTaxWebClientWrapperProperties.getScheme(),
                zipTaxWebClientWrapperProperties.getHost(),
                zipTaxWebClientWrapperProperties.getPath(),
                zipTaxWebClientWrapperProperties.getKey());
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertTrue(zipTaxWebClientWrapper.equals(anotherZipTaxWebClientWrapper) && anotherZipTaxWebClientWrapper.equals(zipTaxWebClientWrapper));
    }


    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(zipTaxWebClientWrapper.hashCode(), anotherZipTaxWebClientWrapper.hashCode());
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = new Address("city", "country", "county", "state", "street", "zip");
        ZipTaxData zipTaxData = new ZipTaxData("test", 1, new ArrayList<>());

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<ZipTaxData>>notNull())).thenReturn(Mono.just(zipTaxData));

        Mono<SalesTaxData> salesTaxDataMono = zipTaxWebClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(salesTaxDataMono).expectNext(zipTaxData).verifyComplete();
    }
}