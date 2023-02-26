package com.complyt.v1.models.properties;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface ExternalIdCheckable {

    BiFunction<ExternalIdCheckable, ServerRequest, Mono<Boolean>> EXTERNAL_ID_CONFLICT_CHECK =
            (externalIdPropertyDto, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("externalId"))
                            .map(externalId -> externalId.equals(externalIdPropertyDto.externalId()));

    String externalId();
}
