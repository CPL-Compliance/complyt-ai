package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.v1.models.ApiKey;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class TokenFacadeTest {
    @InjectMocks
    TokenFacade tokenFacade;

    @Mock
    TokenService tokenService;

    @Mock
    CredentialsService credentialsService;

    @Mock
    AuthorizationService authorizationService;

    ApiKey apiKey;

    Token token;

    Credentials credentials;

    @BeforeEach
    void setup() {
        apiKey = TestUtilities.createApiKey();
        token = TestUtilities.createOutputToken();
        credentials = TestUtilities.createCredentials();
    }

    @Test
    void getToken_TokenExistsInDb_returnToken() {
        // Given
        token = TestUtilities.createOutputToken();

        // When
        when(tokenService.findByApiKey(any())).thenReturn(Mono.just(token));
        when(credentialsService.getCredentialsByApiKey(any())).thenReturn(Mono.just(credentials));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getToken(apiKey);

        StepVerifier.create(actualtokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void getToken_TokenNotExistsInDb_returnToken() {
        // Given
        token = TestUtilities.createOutputToken();
        Token somethingElseToken = token.withAccessToken("Something else");

        // When
        when(tokenService.findByApiKey(any())).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsByApiKey(any())).thenReturn(Mono.just(credentials));
        when(authorizationService.getToken(any())).thenReturn(Mono.just(somethingElseToken));
        when(tokenService.saveToken(any())).thenReturn(Mono.just(somethingElseToken));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getToken(apiKey);

        StepVerifier.create(actualtokenMono).expectNext(somethingElseToken).verifyComplete();
    }

    @Test
    void getToken_apiKeyIsNull_throwNullException() {
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenFacade.getToken(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }
}