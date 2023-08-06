package com.complyt.v1.models.checkables;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface StateCheckable {

    BiFunction<StateCheckable, ServerRequest, Mono<String>> STATE_CONFLICT_CHECK =
            (stateCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("state"))
                            .flatMap(state -> state.equals(stateCheckable.state().name()) || state.equals(stateCheckable.state().abbreviation()) ?
                                    Mono.empty() : Mono.just("state " + DtoErrorMessages.STATE_CONFLICTED_WITH_URL_ERROR));

    StateDto state();
}
