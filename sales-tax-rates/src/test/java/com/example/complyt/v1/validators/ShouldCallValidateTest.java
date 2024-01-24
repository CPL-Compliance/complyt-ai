package com.example.complyt.v1.validators;


import com.complyt.v1.validators.ShouldCallValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShouldCallValidateTest {

    ShouldCallValidate shouldCallValidate;

    @MockBean
    ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        Map<HttpMethod, String> methodsMapValidate = new HashMap<>();
        methodsMapValidate.put(HttpMethod.GET, "/api/resource");

        shouldCallValidate = new ShouldCallValidate(methodsMapValidate);

        serverRequest = mock(ServerRequest.class);
    }

    @Test
    void shouldReturnTrueForValidRequest() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/api/resource");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForInvalidRequestMethod() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/api/resource");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForInvalidRequestPath() {
        // Given
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/api/invalid");

        // When
        boolean result = shouldCallValidate.apply(serverRequest);

        // Then
        assertFalse(result);
    }
}
