package com.complyt.business.vat_validation;

import com.complyt.business.vat_validation.web_clients.VowVatValidationWebClientWrapper;
import com.complyt.config.WebClientWrapperProperties;
import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VowVatVatlidationWebClientWrapperTest {

    @InjectMocks
    VowVatValidationWebClientWrapper vowVatValidationWebClientWrapper;

    @InjectMocks
    VowVatValidationWebClientWrapper anotherVowVatValidationWebClientWrapper;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties vowVatValidationWebClientWrapperProperties;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;




    @BeforeEach
    void setUp() {

        // initializing vowVatValidationWebClientWrapper
        {
            when(vowVatValidationWebClientWrapperProperties.getScheme()).thenReturn("scheme");
            when(vowVatValidationWebClientWrapperProperties.getHost()).thenReturn("host");
            when(vowVatValidationWebClientWrapperProperties.getPath()).thenReturn("path");

            vowVatValidationWebClientWrapper = new VowVatValidationWebClientWrapper(webClient,
                    vowVatValidationWebClientWrapperProperties.getScheme(),
                    vowVatValidationWebClientWrapperProperties.getHost(),
                    vowVatValidationWebClientWrapperProperties.getPath());
        }

        // initializing anotherVowVatValidationWebClientWrapper
        {
            anotherVowVatValidationWebClientWrapper = new VowVatValidationWebClientWrapper(webClient,
                    vowVatValidationWebClientWrapperProperties.getScheme(),
                    vowVatValidationWebClientWrapperProperties.getHost(),
                    vowVatValidationWebClientWrapperProperties.getPath());
        }
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertTrue(vowVatValidationWebClientWrapper.equals(anotherVowVatValidationWebClientWrapper) && anotherVowVatValidationWebClientWrapper.equals(vowVatValidationWebClientWrapper));
    }


    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(vowVatValidationWebClientWrapper.hashCode(), anotherVowVatValidationWebClientWrapper.hashCode());
    }

    @Test
    void validateVat_validVat_ReturnsValidatedVat() {
        // Given
        VatDetailsToValidate vatDetailsToValidate = new VatDetailsToValidate("BE", "0835221567");
        ValidatedVat validatedVat = new ValidatedVat("BE", "Belgium", "0835221567", true, "BV BE³-PROJECTS", "Kasteeldreef 9",
                new Timestamps(LocalDateTime.of(2025, 1, 12, 14, 25), LocalDateTime.of(2025, 1, 12, 14, 25)));

        // When
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class), eq(ValidatedVat.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidatedVat.class)).thenReturn(Mono.just(validatedVat));

        Mono<ValidatedVat> validatedVatMono = vowVatValidationWebClientWrapper.validate(vatDetailsToValidate);

        // Then
        StepVerifier.create(validatedVatMono).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void validateVat_validVatDetails_ReturnsValidatedVat() {
        // Given
        VatDetailsToValidate vatDetailsToValidate = new VatDetailsToValidate("BE", "0835221567");
        ValidatedVat validatedVat = new ValidatedVat("BE", "Belgium", "0835221567", true, "BV BE³-PROJECTS", "Kasteeldreef 9",
                new Timestamps(LocalDateTime.of(2025, 1, 12, 14, 25), LocalDateTime.of(2025, 1, 12, 14, 25)));

        // When
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class), eq(ValidatedVat.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidatedVat.class)).thenReturn(Mono.just(validatedVat));

        Mono<ValidatedVat> validatedVatMono = vowVatValidationWebClientWrapper.validate(vatDetailsToValidate.getVatNumber(), vatDetailsToValidate.getCountryCode());

        // Then
        StepVerifier.create(validatedVatMono).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void validateVat_WebClientError_ThrowsMonoError() {
        // Given
        VatDetailsToValidate vatDetailsToValidate = new VatDetailsToValidate("BE", "0835221567");
        ValidatedVat validatedVat = new ValidatedVat("BE", "Belgium", "0835221567", true, "BV BE³-PROJECTS", "Kasteeldreef 9",
                new Timestamps(LocalDateTime.of(2025, 1, 12, 14, 25), LocalDateTime.of(2025, 1, 12, 14, 25)));

        // When
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class), eq(ValidatedVat.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ValidatedVat.class))
                .thenReturn(Mono.error(new RuntimeException("Simulated Error")));


        Mono<ValidatedVat> validatedVatMono = vowVatValidationWebClientWrapper.validate(vatDetailsToValidate);

        // Then
        StepVerifier.create(validatedVatMono).expectErrorMatches(throwable ->
            throwable instanceof RuntimeException
                && throwable.getMessage().equals("5 Retries Exhausted")
        ).verify();
//        StepVerifier.create(validatedVatMono).expectNext(validatedVat).verifyComplete();

    }
}
