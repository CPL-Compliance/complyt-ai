package com.complyt.v1.validators;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ShouldCallValidateTest {

    ShouldCallValidate shouldCallValidate;

    @MockBean
    ServerRequest serverRequest;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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
