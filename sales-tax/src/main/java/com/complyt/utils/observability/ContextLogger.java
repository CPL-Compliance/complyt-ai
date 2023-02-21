package com.complyt.utils.observability;

import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class ContextLogger {
    public static Mono<Object> observeCtx(String data, Consumer<String> log) {
        return Mono.deferContextual(contextView -> {
            try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(contextView, ObservationThreadLocalAccessor.KEY)) {
                log.accept(data);
                return Mono.empty();
            }
        });
    }
}
