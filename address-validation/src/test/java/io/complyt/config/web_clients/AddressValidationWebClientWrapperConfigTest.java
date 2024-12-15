package io.complyt.config.web_clients;

import io.complyt.business.webclients.addressvalidations.HereAddressValidationClientWrapper;
import io.complyt.business.webclients.addressvalidations.HereStubAddressValidationWebClientWrapper;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressValidationWebClientWrapperConfigTest {

    @InjectMocks
    AddressValidationWebClientWrapperConfig addressValidationWebClientWrapperConfig;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties hereWebClientWrapperProperties;


    @Test
    void hereAddressValidationClientWrapper_CreateInstance_ReturnInstance() {
        // Given
        HereAddressValidationClientWrapper expectedHereAddressValidationClientWrapper = new HereAddressValidationClientWrapper(webClient,
                hereWebClientWrapperProperties.getScheme(),
                hereWebClientWrapperProperties.getHost(),
                hereWebClientWrapperProperties.getPath(),
                hereWebClientWrapperProperties.getKey());

        // When
        when(hereWebClientWrapperProperties.getScheme()).thenReturn("scheme");
        when(hereWebClientWrapperProperties.getHost()).thenReturn("host");
        when(hereWebClientWrapperProperties.getPath()).thenReturn("path");
        when(hereWebClientWrapperProperties.getKey()).thenReturn(new Pair<>("key", "test-value"));

        HereAddressValidationClientWrapper actualHereAddressValidationClientWrapper =
                addressValidationWebClientWrapperConfig.hereAddressValidationClientWrapper(webClient, hereWebClientWrapperProperties);

        // Then
        assertEquals(expectedHereAddressValidationClientWrapper, actualHereAddressValidationClientWrapper);
    }

    @Test void stubHereAddressValidationClientWrapper_CreateInstance_ReturnInstance() {
        // Given
        HereStubAddressValidationWebClientWrapper expectedHereStubAddressValidationWebClientWrapper =
                new HereStubAddressValidationWebClientWrapper(null, "", "", "", new Pair<>("",""));

        // When
        HereStubAddressValidationWebClientWrapper actualHereStubAddressValidationWebClientWrapper = addressValidationWebClientWrapperConfig.stubHereAddressValidationWebClientWrapper();

        // Then

        assertEquals(expectedHereStubAddressValidationWebClientWrapper,actualHereStubAddressValidationWebClientWrapper);
    }

    @Test
    void hereAddressValidationClientWrapper_NullWebClient_ThrowsException() {
        // Given
        WebClient nullWebClient = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressValidationWebClientWrapperConfig.hereAddressValidationClientWrapper(nullWebClient, hereWebClientWrapperProperties);
        });

        assertEquals("webClient is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void hereAddressValidationClientWrapper_NullWebClientWrapperProperties_ThrowsException() {
        // Given
        WebClient webClient = WebClient.builder().build(); // Replace with actual instantiation if necessary
        WebClientWrapperProperties nullHereWebClientWrapperProperties = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressValidationWebClientWrapperConfig.hereAddressValidationClientWrapper(webClient, nullHereWebClientWrapperProperties);
        });

        assertEquals("hereWebClientWrapperProperties is marked non-null but is null", nullPointerException.getMessage());
    }

}