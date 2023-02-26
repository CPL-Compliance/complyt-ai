package com.complyt.v1.models.checkables;

import com.complyt.v1.models.StateDto;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface StateCheckable {

    BiFunction<StateCheckable, ServerRequest, Mono<Boolean>> STATE_CONFLICT_CHECK =
            (stateCheckable, serverRequest) ->
                    Mono.just(serverRequest.pathVariable("state"))
                            .map(state -> state.equals(stateCheckable.state().name()) || state.equals(stateCheckable.state().abbreviation()));

    StateDto state();
}
