package com.complyt.v1.models.checkables;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface SourceCheckable {

    BiFunction<SourceCheckable, ServerRequest, Mono<String>> SOURCE_CONFLICT_CHECK =
            (sourceCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("source"))
                            .flatMap(source -> source.equals(sourceCheckable.source()) ?
                                    Mono.empty() : Mono.just("source " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR));

    String source();
}
