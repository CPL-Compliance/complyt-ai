package com.complyt.business.sales_tax_web_clients;

import com.complyt.config.WebClientWrapperProperties;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.exceptions.types.ComplytApiException;
import com.complyt.v1.exceptions.types.fastTax.FastTaxError;
import com.complyt.v1.exceptions.types.fastTax.FastTaxException;
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

import java.lang.reflect.Method;
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
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("test", new ArrayList<>(), "1", null);

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

    @Test
    void handleResponse_MatchLevelError_InvalidInput_ThrowsFastTaxException() throws Exception {
        // Given
        FastTaxError fastTaxError = new FastTaxError("Invalid address", "2");
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("Error", new ArrayList<>(), "1", fastTaxError);

        // Access the private method via reflection
        Method handleResponseMethod = FastTaxGetBestMatchWebClientWrapper.class.getDeclaredMethod("handleResponse", FastTaxGetBestMatchData.class);
        handleResponseMethod.setAccessible(true);  // Make private method accessible

        // When
        Mono<SalesTaxData> resultMono = (Mono<SalesTaxData>) handleResponseMethod.invoke(fastTaxGetBestMatchWebClientWrapper, fastTaxGetBestMatchData);

        // Then
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof FastTaxException &&
                        throwable.getMessage().contains("Invalid address"))
                .verify();
    }

    @Test
    void handleResponse_MatchLevelError_OtherError_ThrowsComplytApiException() throws Exception {
        // Given
        FastTaxError fastTaxError = new FastTaxError("Some other error", "3");
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("Error", new ArrayList<>(), "1", fastTaxError);

        // Access the private method via reflection
        Method handleResponseMethod = FastTaxGetBestMatchWebClientWrapper.class.getDeclaredMethod("handleResponse", FastTaxGetBestMatchData.class);
        handleResponseMethod.setAccessible(true);  // Make private method accessible

        // When
        Mono<SalesTaxData> resultMono = (Mono<SalesTaxData>) handleResponseMethod.invoke(fastTaxGetBestMatchWebClientWrapper, fastTaxGetBestMatchData);

        // Then
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof ComplytApiException &&
                        throwable.getMessage().contains("The request failed due to an internal error. Please contact support@complyt.io if this continues"))  // Check for internal server error
                .verify();
    }

    @Test
    void handleResponse_ValidResponse_ReturnsMonoWithSalesTaxData() throws Exception {
        // Given
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("Valid", new ArrayList<>(), "1", null);  // No error

        // Access the private method via reflection
        Method handleResponseMethod = FastTaxGetBestMatchWebClientWrapper.class.getDeclaredMethod("handleResponse", FastTaxGetBestMatchData.class);
        handleResponseMethod.setAccessible(true);  // Make private method accessible

        // When
        Mono<SalesTaxData> resultMono = (Mono<SalesTaxData>) handleResponseMethod.invoke(fastTaxGetBestMatchWebClientWrapper, fastTaxGetBestMatchData);

        // Then
        StepVerifier.create(resultMono)
                .expectNext(fastTaxGetBestMatchData)  // Should return the original response
                .verifyComplete();
    }

    @Test
    void findByAddress_matchLevelErrorInvalidInput_ThrowsFastTaxException() {
        // Given
        String zip = "12345";
        String address = "123 Main St";
        String city = "Test City";
        String state = "Test State";

        FastTaxError error = new FastTaxError("Invalid input description", "2");
        FastTaxGetBestMatchData errorResponse = new FastTaxGetBestMatchData("Error", null, "1", error);

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<FastTaxGetBestMatchData>>notNull()))
                .thenReturn(Mono.just(errorResponse));

        // When
        Mono<SalesTaxData> result = fastTaxGetBestMatchWebClientWrapper.findByAddress(zip, address, city, state);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof FastTaxException &&
                        throwable.getMessage().contains("Invalid input description"))
                .verify();
    }

    @Test
    void findByAddress_matchLevelErrorGeneric_ThrowsComplytApiException() {
        // Given
        String zip = "12345";
        String address = "123 Main St";
        String city = "Test City";
        String state = "Test State";

        FastTaxError error = new FastTaxError("Generic error description", "99");
        FastTaxGetBestMatchData errorResponse = new FastTaxGetBestMatchData("Error", null, "1", error);

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<FastTaxGetBestMatchData>>notNull()))
                .thenReturn(Mono.just(errorResponse));

        // When
        Mono<SalesTaxData> result = fastTaxGetBestMatchWebClientWrapper.findByAddress(zip, address, city, state);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ComplytApiException &&
                        throwable.getMessage().contains(GenericErrorMessages.INTERNAL_SERVER_ERROR))
                .verify();
    }

}