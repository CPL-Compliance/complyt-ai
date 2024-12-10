package com.complyt.v1.validators.query_params;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class QueryParamsExtractorEmptyTest {

    @Test
    void extract_ShouldReturnEmptyMono() {
        // Arrange
        QueryParamsExtractorEmpty<Object> queryParamsExtractorEmpty = new QueryParamsExtractorEmpty<>();
        ServerRequest mockServerRequest = mock(ServerRequest.class);

        // Act
        Mono<Object> result = queryParamsExtractorEmpty.extract(mockServerRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}