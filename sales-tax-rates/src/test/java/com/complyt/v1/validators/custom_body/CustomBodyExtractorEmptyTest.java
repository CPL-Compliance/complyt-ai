package com.complyt.v1.validators.custom_body;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CustomBodyExtractorEmptyTest {

    @Test
    void extract_ShouldReturnEmptyMono() {
        // Arrange
        CustomBodyExtractorEmpty<Object> customBodyExtractorEmpty = new CustomBodyExtractorEmpty<>();
        ServerRequest mockServerRequest = mock(ServerRequest.class);

        // Act
        Mono<Object> result = customBodyExtractorEmpty.extract(mockServerRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}