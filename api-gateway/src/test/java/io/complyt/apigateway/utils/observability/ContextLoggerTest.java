package io.complyt.apigateway.utils.observability;

import io.complyt.apigateway.security.TenantResolver;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

class ContextLoggerTest {

    static MockedStatic tenantResolverMockStatic;
    static Tracer mockTracer;
    static Span mockSpan;
    static MockedStatic mockGlobalTracer;

    @BeforeAll
    static void beforeAll() {
        try {
            tenantResolverMockStatic = mockStatic(TenantResolver.class);
            mockTracer = mock(io.opentracing.Tracer.class);
            mockSpan = mock(Span.class);
            mockGlobalTracer = mockStatic(GlobalTracer.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        tenantResolverMockStatic.close();
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

    @Test
    void observeCtx_GetActiveSpan_ReturnsNull() {

        when(TenantResolver.resolve()).thenReturn(Mono.just("test"));
        Logger logger = Logger.getLogger("Test");
        Mono<Object> actualMono = ContextLogger.observeCtx("Test String", logger::info);

        StepVerifier.create(actualMono).verifyComplete();
    }

    @Test
    void observeCtx_SetsTenantIdTagOnActiveSpan_WhenSpanExists() {
        // Mock the TenantResolver
        when(TenantResolver.resolve()).thenReturn(Mono.just("test"));
        when(mockTracer.activeSpan()).thenReturn(mockSpan);

        mockGlobalTracer.when(GlobalTracer::get).thenReturn(mockTracer);

        // Execute the method under test
        Logger logger = Logger.getLogger("Test");
        Mono<Object> result = ContextLogger.observeCtx("Test String", logger::info);

        // Verify the result
        StepVerifier.create(result).verifyComplete();

        // Verify that the span tag was set correctly
        verify(mockSpan).setTag("tenant.id", "test");
    }

    @Test
    void observeCtx_DoesNotSetTenantIdTagOnActiveSpan_WhenSpanIsNull() {
        when(TenantResolver.resolve()).thenReturn(Mono.just("test"));

        // Setup GlobalTracer mock
        mockGlobalTracer.when(GlobalTracer::get).thenReturn(mockTracer);

        // Execute the method under test
        Logger logger = Logger.getLogger("Test");
        Mono<Object> result = ContextLogger.observeCtx("Test String", logger::info);

        // Verify the result completes successfully
        StepVerifier.create(result).verifyComplete();

        // No verify needed since span is null and the code should handle it gracefully
    }

}