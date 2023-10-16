package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.services.ApiKeyService;
import io.complyt.authentication.services.CredentialsService;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class ApiKeyFacadeTest {
    @InjectMocks
    ApiKeyFacade apiKeyFacade;

    @Mock
    ApiKeyService apiKeyService;

    @Mock
    CredentialsService credentialsService;

    Credentials credentials;

    @BeforeEach
    void setUp() {
        credentials = TestUtilities.createCredentials();
    }

    @Test
    void saveCredentials_validCredentials_returnApiKey() {
        String expectedApiKeyStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1ec-3572db2e-486b-480a-995b-2e4d2b9104fa";
        ApiKey expectedApiKey = new ApiKey(expectedApiKeyStr);

        // When
        when(apiKeyService.generate()).thenReturn(expectedApiKeyStr);
        when(apiKeyService.generatefromString(expectedApiKeyStr)).thenReturn(expectedApiKey);
        when(credentialsService.saveCredentials(credentials, expectedApiKey)).thenReturn(Mono.just(credentials));

        Mono<String> actualApiKey = apiKeyFacade.saveCredentials(credentials);

        StepVerifier.create(actualApiKey)
                .expectNext(expectedApiKeyStr)
                .verifyComplete();
    }

    @Test
    void saveCredentials_credentialsIsNull_throwNullException() {
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            apiKeyFacade.saveCredentials(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }
}