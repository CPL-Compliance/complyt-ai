package com.complyt.v1.models.properties;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface SourceCheckable {

    BiFunction<SourceCheckable, ServerRequest, Mono<Boolean>> SOURCE_CONFLICT_CHECK =
            (sourcePropertyDto, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("source"))
                            .map(source -> source.equals(sourcePropertyDto.source()));

    String source();
}
