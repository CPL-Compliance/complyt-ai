package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.PartnershipService;
import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.v1.exceptions.types.ApiKeyNotValidException;
import io.complyt.authentication.v1.exceptions.types.FailedToCreateJWTException;
import io.complyt.authentication.v1.exceptions.types.TenantNotSupportedException;
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
import test_utils.BaseTestClass;
import test_utils.unit_tests.TestUtilities;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class TokenFacadeTest extends BaseTestClass {
    @InjectMocks
    TokenFacade tokenFacade;

    @Mock
    TokenService tokenService;

    @Mock
    CredentialsService credentialsService;

    @Mock
    AuthorizationService authorizationService;

    @Mock
    PartnershipService partnershipService;

    @Mock
    TenantResolver tenantResolver;

    ApiKey apiKey;

    Token token;

    Credentials credentials;
    String partnerTenantId;
    String requestedTenantId;

    @BeforeEach
    void setup() {
        apiKey = TestUtilities.createApiKey();
        token = TestUtilities.createOutputToken();
        credentials = TestUtilities.createCredentials();
        partnerTenantId = TestUtilities.createTenantId();
        requestedTenantId = TestUtilities.createTenantId();
    }

    @Test
    void getToken_TokenExistsInDb_returnToken() {
        // Given
        token = TestUtilities.createOutputToken();

        // When
        when(tokenService.findByApiKeyAndDecrypt(any())).thenReturn(Mono.just(token));

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
        when(tokenService.findByApiKeyAndDecrypt(any())).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsByApiKeyAndDecrypt(any())).thenReturn(Mono.just(credentials));
        when(authorizationService.getToken(any())).thenReturn(Mono.just(somethingElseToken));
        when(tokenService.saveToken(any(), any())).thenReturn(Mono.just(somethingElseToken));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getToken(apiKey);

        StepVerifier.create(actualtokenMono).expectNext(somethingElseToken).verifyComplete();
    }

    @Test
    void getToken_credentialsNotExists_return401() {
        // When
        when(tokenService.findByApiKeyAndDecrypt(any())).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsByApiKeyAndDecrypt(any())).thenReturn(Mono.empty());


        // Then
        Mono<Token> actualtokenMono = tokenFacade.getToken(apiKey);

        StepVerifier.create(actualtokenMono).expectError(ApiKeyNotValidException.class).verify();
    }

    @Test
    void getTokenForPartnerByTenantId_TokenExistsInDb_returnToken() {
        // Given
        token = TestUtilities.createOutputToken();
        List<String> supportedTenants = List.of(requestedTenantId, "Another Random Tenant Id");

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));
        when(credentialsService.getApiKeyByTenantId(requestedTenantId)).thenReturn(Mono.just(apiKey));
        when(tokenService.findByApiKeyAndTenantIdForPartnerAndDecrypt(apiKey, partnerTenantId)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void getTokenForPartnerByTenantId_TenantNotInSupportedList_returnException() {
        // Given
        List<String> supportedTenants = List.of("Random Tenant Id", "Another Random Tenant Id");

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectError(TenantNotSupportedException.class).verify();
    }

    @Test
    void getTokenForPartnerByTenantId_SupportedListIsEmpty_returnException() {
        // Given
        List<String> supportedTenants = List.of();

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectError(TenantNotSupportedException.class).verify();
    }

    @Test
    void getTokenForPartnerByTenantId_TokenNotExistsInDb_returnToken() {
        // Given
        token = TestUtilities.createOutputToken();
        List<String> supportedTenants = List.of(requestedTenantId, "Another Random Tenant Id");

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));
        when(credentialsService.getApiKeyByTenantId(requestedTenantId)).thenReturn(Mono.just(apiKey));
        when(tokenService.findByApiKeyAndTenantIdForPartnerAndDecrypt(apiKey, partnerTenantId)).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsForPartnerByApiKeyAndDecrypt(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getTokenForPartner(credentials, partnerTenantId)).thenReturn(Mono.just(token));
        when(tokenService.saveToken(token, requestedTenantId, partnerTenantId)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void getTokenForPartnerByTenantId_TokenNotExistsInDbAndFailedToCreateToken_returnException() {
        // Given
        token = TestUtilities.createOutputToken();
        List<String> supportedTenants = List.of(requestedTenantId, "Another Random Tenant Id");

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));
        when(credentialsService.getApiKeyByTenantId(requestedTenantId)).thenReturn(Mono.just(apiKey));
        when(tokenService.findByApiKeyAndTenantIdForPartnerAndDecrypt(apiKey, partnerTenantId)).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsForPartnerByApiKeyAndDecrypt(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getTokenForPartner(credentials, partnerTenantId)).thenReturn(Mono.empty());

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectError(FailedToCreateJWTException.class).verify();
    }

    @Test
    void getTokenForPartnerByTenantId_TokenNotExistsInDbAndCredentialsNotExists_returnException() {
        // Given
        token = TestUtilities.createOutputToken();
        List<String> supportedTenants = List.of(requestedTenantId, "Another Random Tenant Id");

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)).thenReturn(Mono.just(supportedTenants));
        when(credentialsService.getApiKeyByTenantId(requestedTenantId)).thenReturn(Mono.just(apiKey));
        when(tokenService.findByApiKeyAndTenantIdForPartnerAndDecrypt(apiKey, partnerTenantId)).thenReturn(Mono.empty());
        when(credentialsService.getCredentialsForPartnerByApiKeyAndDecrypt(apiKey)).thenReturn(Mono.empty());

        // Then
        Mono<Token> actualtokenMono = tokenFacade.getTokenForPartnerByTenantId(requestedTenantId);

        StepVerifier.create(actualtokenMono).expectError(FailedToCreateJWTException.class).verify();
    }


    @Test
    void getTokenForPartnerByTenantId_tenantIdIsNull_throwNullException() {
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenFacade.getTokenForPartnerByTenantId(null);
        });

        assertEquals(nullPointerException.getMessage(), "requestedTenantId is marked non-null but is null");
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