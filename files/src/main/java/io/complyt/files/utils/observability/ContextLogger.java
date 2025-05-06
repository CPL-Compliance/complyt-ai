package io.complyt.files.utils.observability;

import io.complyt.files.security.TenantResolver;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class ContextLogger {
    public static Mono<Object> observeCtx(String data, Consumer<String> log) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    return Mono.deferContextual(contextView -> {
                        // Set the tenantId in MDC
                        MDC.put("tenantId", tenantId);
                        try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(contextView, ObservationThreadLocalAccessor.KEY)) {
                            log.accept(data);
                            return Mono.empty();
                        } finally {
                            // Clear the MDC to prevent memory leaks
                            MDC.remove("tenantId");
                        }
                    });
                });
    }
}
