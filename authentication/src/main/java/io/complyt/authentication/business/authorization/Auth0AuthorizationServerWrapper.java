package io.complyt.authentication.business.authorization;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.auth0_client.ClientMetadata;
import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import io.complyt.authentication.domain.mappers.Auth0AccessTokenToAccessToken;
import io.complyt.authentication.utils.observability.ContextLogger;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class Auth0AuthorizationServerWrapper implements AuthorizationServerWrapper {
    @NonNull
    WebClient webClient;

    @NonNull
    String managementAudience;

    @NonNull
    String grantType;

    @NonNull
    String adminClientId;

    @NonNull
    String adminClientSecret;

    @Override
    public Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                            final @NonNull String audience, final @NonNull String grantType) {
        return ContextLogger.observeCtx("Getting Auth0 Token", log::info)
                .then(webClient.post()
                        .uri("/oauth/token")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .bodyValue("client_id=" + clientId +
                                "&client_secret=" + clientSecret +
                                "&audience=" + audience +
                                "&grant_type=" + grantType)
                        .retrieve()
                        .bodyToMono(Auth0AccessToken.class)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted"))))
                        .map(Auth0AccessTokenToAccessToken.INSTANCE::map));
    }

    /*
    When newClientId/newClientSecret is null the metadata is deleted, otherwise it changes the metadata
     */
    public Mono<Auth0Client> updateApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                                    final @NonNull String tenantId, final @NonNull String accessToken,
                                                    final String newClientId, final String newClientSecret) {

        Auth0ClientMetaData auth0ClientMetaData = new Auth0ClientMetaData(clientName,
                new ClientMetadata(tenantId,newClientId, newClientSecret));

        System.out.println("outing:" + auth0ClientMetaData.getAsJson());
        return ContextLogger.observeCtx("Removing Auth0 Api-Key metadata", log::info)
                .then(webClient.patch()
                        .uri("/api/v2/clients/" + clientId)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + accessToken)
                        .bodyValue(auth0ClientMetaData.getAsJson())
                        .retrieve()
                        .bodyToMono(Auth0Client.class))
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted"))));
    }

    public Mono<AccessToken> getManagementAccessToken() {
        return ContextLogger.observeCtx("Getting Auth0 Management Token", log::info)
                .then(webClient.post()
                        .uri("/oauth/token")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .bodyValue("client_id=" + adminClientId +
                                "&client_secret=" + adminClientSecret +
                                "&audience=" + managementAudience +
                                "&grant_type=" + grantType)
                        .retrieve()
                        .bodyToMono(Auth0AccessToken.class)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted"))))
                        .map(Auth0AccessTokenToAccessToken.INSTANCE::map));
    }

    public Mono<TenantIdAndNameObject> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull String accessToken) {
        return ContextLogger.observeCtx("Getting TenantId And ClientName From Auth0", log::info)
                .then(webClient.get()
                        .uri("/api/v2/clients/" + clientId)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Auth0Client.class)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted")))))
                .map(auth0Client -> new TenantIdAndNameObject(auth0Client.client_metadata().getTenant_id(), auth0Client.name()));
    }
}