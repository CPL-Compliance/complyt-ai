package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.v1.models.ApiKeyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryParamsExtractorCredentialsTest {

    private QueryParamsExtractorCredentials extractor;

    @BeforeEach
    void setUp() {
        extractor = new QueryParamsExtractorCredentials();
    }

    @Test
    void extract_MediaTypeIsFormUrlEncoded_ReturnApiKeyDto() {
        ServerRequest.Headers headers = mock(ServerRequest.Headers.class);
        when(headers.contentType()).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));

        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", "test-client-id");
        formData.add("clientSecret", "test-client-secret");

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.headers()).thenReturn(headers);
        when(serverRequest.formData()).thenReturn(Mono.just(formData));

        Mono<ApiKeyDto> result = extractor.extract(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        "test-client-id".equals(dto.clientId()) &&
                                "test-client-secret".equals(dto.clientSecret()))
                .verifyComplete();
    }

    @Test
    void extract_MediaTypeIsNotPresent_ReturnEmpty() {
        ServerRequest.Headers headers = mock(ServerRequest.Headers.class);
        when(headers.contentType()).thenReturn(Optional.empty());

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.headers()).thenReturn(headers);

        Mono<ApiKeyDto> result = extractor.extract(serverRequest);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void extract_MediaTypeIsNotFormUrlEncoded_ReturnEmpty() {
        ServerRequest.Headers headers = mock(ServerRequest.Headers.class);
        when(headers.contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.headers()).thenReturn(headers);

        Mono<ApiKeyDto> result = extractor.extract(serverRequest);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
