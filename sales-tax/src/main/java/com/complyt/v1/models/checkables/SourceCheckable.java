package com.complyt.v1.models.checkables;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface SourceCheckable {

    BiFunction<SourceCheckable, ServerRequest, Mono<Boolean>> SOURCE_CONFLICT_CHECK =
            (sourceCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("source"))
                            .map(source -> source.equals(sourceCheckable.source()));

    String source();
}
