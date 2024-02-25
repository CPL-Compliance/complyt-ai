package com.complyt.v1.models.checkables;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface TenantIdCheckable {
    BiFunction<TenantIdCheckable, ServerRequest, Mono<String>> TENANT_ID_CONFLICT_CHECK =
            (sourceCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("tenantId"))
                            .flatMap(source -> source.equals(sourceCheckable.tenantId()) ?
                                    Mono.empty() : Mono.just("tenantId " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR));

    String tenantId();
}