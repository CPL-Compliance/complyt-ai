package io.complyt.authentication.business.authorization;

import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.domain.mappers.Auth0AccessTokenToAccessToken;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    public Mono<Auth0Client> removeApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                                    final @NonNull String tenantId) {
        String json = "{ name: " + clientName +
                ", client_metadata: { tenant_id: " + tenantId +
                ", clientId : " + null + ", clientId : " + null + "}}";

        return ContextLogger.observeCtx("Removing Auth0 Api-Key", log::info)
                .then(webClient.patch()
                        .uri("/api/v2/clients/" + clientId)
                        .header("Content-Type", "application/json")
                        .bodyValue(json)
                        .retrieve()
                        .bodyToMono(Auth0Client.class)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted")))));
//                        .map(Auth0AccessTokenToAccessToken.INSTANCE::map));
    }

    public Mono<AccessToken> getManagementAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                                      final @NonNull String audience, final @NonNull String grantType) {


        return ContextLogger.observeCtx("Getting Auth0 Management Token", log::info)
                .then(webClient.post()
                        .uri("oauth/token")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .bodyValue("client_id=LidAB8xGqfzDL6pzeEH6eWmqlTEZ0gQr" +
                                "&client_secret=y6g3jHGW0FneBg4zCnqAA2P_SEDe8UUKpvH1DcX3wkS4fCX3Bdty64zjqwhAxrQ-" +
                                "&audience=" + audience +
                                "&grant_type=" + grantType)
                        .retrieve()
                        .bodyToMono(Auth0AccessToken.class)
                        .map(x -> x)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted"))))
                        .map(Auth0AccessTokenToAccessToken.INSTANCE::map));
    }

    public Mono<Auth0Client> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull AccessToken accessToken) {
        return ContextLogger.observeCtx("Getting TenantId And ClientName From Auth0", log::info)
                .then(webClient.post()
                        .uri("/api/v2/clients/" + clientId)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Auth0Client.class)
                        .map(x -> x)
                        .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                        new ComplytAuth0Exception(retrySignal.totalRetries() + " Retries Exhausted")))));
    }

}