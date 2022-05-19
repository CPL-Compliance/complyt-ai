package com.complyt.business.sales_tax;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FastTaxWebClientWrapperTest {
    FastTaxWebClientWrapper fastTaxWebClientWrapper;
    FastTaxWebClientWrapper anotherFastTaxWebClientWrapper;

    @Mock
    WebClient webClient;

    @BeforeEach
    void setUp() {
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");
        RestTemplate restTemplate = new RestTemplate();

        fastTaxWebClientWrapper = new FastTaxWebClientWrapper(webClient, scheme, host, path, key);

//        webClient = WebClient.builder().baseUrl(uri.toString()).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
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

    void findByAddress_validAddress_ReturnsSalesTaxData() {

    }
}