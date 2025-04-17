package io.complyt.apigateway.utils.observability;

import io.complyt.apigateway.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.logging.Logger;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ContextLoggerTest {

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

    @Test
    void ContextLogger_CreateInstance() {
        ContextLogger contextLogger = new ContextLogger();
    }

    @Test
    void observeCtx_PrintsLogInContextTenantNotMocked_ReturnsMono() {
        Logger logger = Logger.getLogger("Test");
        Mono<Object> actualMono = ContextLogger.observeCtx("Test String", logger::info);

        StepVerifier.create(actualMono).verifyComplete();
    }

    @Test
    void observeCtx_PrintsLogInContext_ReturnsMono() {
        when(TenantResolver.resolve()).thenReturn(Mono.just("test"));
        Logger logger = Logger.getLogger("Test");
        Mono<Object> actualMono = ContextLogger.observeCtx("Test String", logger::info);

        StepVerifier.create(actualMono).verifyComplete();
    }
}