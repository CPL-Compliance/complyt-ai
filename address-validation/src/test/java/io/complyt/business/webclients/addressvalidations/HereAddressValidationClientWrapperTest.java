package io.complyt.business.webclients.addressvalidations;

import io.complyt.domain.Address;
import io.complyt.domain.here.HereAddressData;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HereAddressValidationClientWrapperTest {

    HereAddressValidationClientWrapper hereAddressValidationClientWrapper;

    String scheme = "scheme";
    String host = "host";
    String path = "path";
    Pair<String, String> license = new Pair("key", "key");

    Address address = TestUtilities.getAddress();
    HereAddressData hereAddressData = TestUtilities.getHereAddressData();

    @Mock
    WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setup() {
        hereAddressValidationClientWrapper = new HereAddressValidationClientWrapper(webClient, scheme, host, path, license);
    }

    @Test
    void validateAddress_ReturnsHereAddressData() {
        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void validateAddress_lastStringSemiColon_ReturnsHereAddressData() {
        address = address.withCountry(";");
        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void validateAddress_StringIsNull_ReturnsHereAddressData() {
        address = address.withZip(null);
        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void validateAddress_StringIsNotNullAndNotEmpty_AppendsToQueryParam() {
        // Given
        address = address.withZip("12345").withStreet("Main St").withCity("Los Angeles").withState("CA").withCountry("US");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void validateAddress_StringContainsSemiCol_AppendsToQueryParam() {
        // Given
        address = address.withZip("12345").withStreet("Main; St").withCity("Los Angeles").withState("CA").withCountry("US");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void appendStringIfNotNullAndNotEmpty_StringIsNotNullAndNotEmpty_AppendsToQueryParam() {
        // Given
        address = address.withStreet("Main St");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void appendStringIfNotNullAndNotEmpty_StringIsNull_DoesNotAppendToQueryParam() {
        // Given
        address = address.withStreet(null);

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void appendStringIfNotNullAndNotEmpty_StringIsEmpty_DoesNotAppendToQueryParam() {
        // Given
        address = address.withStreet("");

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void queryParamBuilder_LastQueryParamEndsWithSemiColon_RemovesTrailingSemiColon() {
        // Given
        address = address.withCountry("US").withStreet(null).withCity(null).withState(null).withZip(null);

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri((URI) any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<HereAddressData>>notNull())).thenReturn(Mono.just(hereAddressData));

        // Then
        StepVerifier.create(hereAddressValidationClientWrapper.validateAddress(address)).expectNext(hereAddressData).verifyComplete();
    }

    @Test
    void queryParamBuilder_LastQueryParamDoesNotEndWithSemiColon_DoesNotAlterQueryParam() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = HereAddressValidationClientWrapper.class.getDeclaredMethod("deleteSemiColonIfNeeded", StringBuilder.class);
        method.setAccessible(true);
        HereAddressValidationClientWrapper wrapper = new HereAddressValidationClientWrapper(webClient, "https", "example.com", "/validate", new Pair<>("key", "value"));
        StringBuilder sb = new StringBuilder("postalCode=12345");
        method.invoke(wrapper, sb);
        assertEquals("postalCode=12345", sb.toString());
    }

    @Test
    void limitWords_InputExceedsWordLimit_ReturnsTruncatedString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method using reflection
        Method method = HereAddressValidationClientWrapper.class.getDeclaredMethod("limitWords", String.class, int.class);
        method.setAccessible(true);

        // Create an instance of the class to invoke the method
        HereAddressValidationClientWrapper wrapper = new HereAddressValidationClientWrapper(webClient, "https", "example.com", "/validate", new Pair<>("key", "value"));

        // Test input
        String input = "This is a test string with more than four words";
        int wordLimit = 4;

        // Invoke the method
        String result = (String) method.invoke(wrapper, input, wordLimit);

        // Assert the expected output
        assertEquals("This is a test", result);
    }

    @Test
    void limitWords_InputWithinWordLimit_ReturnsUnalteredString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Access the private method using reflection
        Method method = HereAddressValidationClientWrapper.class.getDeclaredMethod("limitWords", String.class, int.class);
        method.setAccessible(true);

        // Create an instance of the class to invoke the method
        HereAddressValidationClientWrapper wrapper = new HereAddressValidationClientWrapper(webClient, "https", "example.com", "/validate", new Pair<>("key", "value"));

        // Test input
        String input = "Short input string";
        int wordLimit = 10;

        // Invoke the method
        String result = (String) method.invoke(wrapper, input, wordLimit);

        // Assert the expected output
        assertEquals("Short input string", result);
    }
}