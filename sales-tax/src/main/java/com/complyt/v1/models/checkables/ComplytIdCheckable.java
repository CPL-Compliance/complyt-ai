package com.complyt.v1.models.checkables;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;

public interface ComplytIdCheckable {

    BiFunction<ComplytIdCheckable, ServerRequest, Mono<Boolean>> COMPLYT_ID_CONFLICT_CHECK =
            (complytIdCheckable, serverRequest) ->
                    Mono.just(UUID.fromString(serverRequest.pathVariable("complytId")))
                            .map(complytId -> complytId.equals(complytIdCheckable.complytId()));

    UUID complytId();
}
