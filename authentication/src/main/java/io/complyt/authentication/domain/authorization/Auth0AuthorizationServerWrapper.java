package io.complyt.authentication.domain.authorization;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auth0AuthorizationServerWrapper implements AuthorizationServerWrapper {
    @NonNull
    @Value("{authorization-server-url}")
    String serverUrl;

    @NonNull
    WebClient webClient;

    public Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                            final @NonNull String audience, final @NonNull String grantType) {
        MultipartBodyBuilder builder = getMultipartBodyBuilder(clientId, clientSecret, audience, grantType);

        return webClient
                .post()
                .uri(serverUrl)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(AccessToken.class);
    }

    @NonNull
    private MultipartBodyBuilder getMultipartBodyBuilder(@NonNull String clientId, @NonNull String clientSecret,
                                                         @NonNull String audience, @NonNull String grantType) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("clientId", clientId);
        builder.part("clientSecret", clientSecret);
        builder.part("audience", audience);
        builder.part("grantType", grantType);

        return builder;
    }
}
