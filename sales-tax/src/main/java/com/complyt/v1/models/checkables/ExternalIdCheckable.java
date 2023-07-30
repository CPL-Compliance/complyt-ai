package com.complyt.v1.models.checkables;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface ExternalIdCheckable {

    BiFunction<ExternalIdCheckable, ServerRequest, Mono<String>> EXTERNAL_ID_CONFLICT_CHECK =
            (externalIdCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("externalId"))
                            .flatMap(externalId -> externalId.equals(externalIdCheckable.externalId()) ?
                                    Mono.empty() : Mono.just("externalId " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR));

    String externalId();
}
