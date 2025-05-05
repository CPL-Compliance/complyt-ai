package io.complyt.apigateway.utils.observability;

import io.complyt.apigateway.security.TenantResolver;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.slf4j.MDC;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class ContextLogger {
    public static Mono<Object> observeCtx(String data, Consumer<String> log) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> Mono.deferContextual(contextView -> {
                    // Set tenantId in MDC for logging
                    MDC.put("tenantId", tenantId);

                    // Set tenantId as a trace tag on the current span
                    Span span = GlobalTracer.get().activeSpan();
                    if (span != null) {
                        span.setTag("tenant.id", tenantId);
                    }

                    // Restore thread-local context for observability
                    try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(contextView, ObservationThreadLocalAccessor.KEY)) {
                        log.accept(data);
                        return Mono.empty();
                    } finally {
                        // Always clear MDC to avoid memory leaks in reactive context
                        MDC.remove("tenantId");
                    }
                }));
    }
}
