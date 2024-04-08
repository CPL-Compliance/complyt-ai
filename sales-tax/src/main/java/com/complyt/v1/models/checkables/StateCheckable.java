package com.complyt.v1.models.checkables;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Generated
public interface StateCheckable {

    BiFunction<StateCheckable, ServerRequest, Mono<String>> STATE_CONFLICT_CHECK =
            (stateCheckable, serverRequest) ->
                    Mono.just(serverRequest.queryParam("state").orElse(""))
                            .flatMap(state -> stateNotNullIfPresentInQueryParam(stateCheckable, state) && (state.equals(stateCheckable.state().name()) || state.equals(stateCheckable.state().abbreviation())) ?
                                    Mono.empty() : Mono.just("state " + DtoErrorMessages.CONFLICTED_WITH_QUERY_PARAM_IN_URL_ERROR));

    private static boolean stateNotNullIfPresentInQueryParam(StateCheckable stateCheckable, String stateQueryParam) {
        return stateQueryParam != "" && stateCheckable.state() != null;
    }

    StateDto state();
}
