package io.complyt.authentication.business.authorization;

import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.mappers.Auth0AccessTokenToAccessToken;
import io.complyt.authentication.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
}