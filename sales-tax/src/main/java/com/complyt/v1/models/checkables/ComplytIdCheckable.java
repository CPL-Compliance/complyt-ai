package com.complyt.v1.models.checkables;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;

public interface ComplytIdCheckable {

    BiFunction<ComplytIdCheckable, ServerRequest, Mono<String>> COMPLYT_ID_CONFLICT_CHECK =
            (complytIdCheckable, serverRequest) ->
                    Mono.just(UUID.fromString(serverRequest.pathVariable("complytId")))
                            .flatMap(complytId -> complytId.equals(complytIdCheckable.complytId()) ?
                                    Mono.empty() : Mono.just("complytId " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR));

    UUID complytId();
}
