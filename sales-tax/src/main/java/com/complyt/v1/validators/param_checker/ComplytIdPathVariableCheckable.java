package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.authentication.v1.config.regex.UUID_REGEXP;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Function;


public interface ComplytIdPathVariableCheckable {

    Function<ServerRequest, Mono<String>> COMPLYT_ID_CHECK =
            (serverRequest) ->
                Mono.just(serverRequest.pathVariable("complytId"))
                        .flatMap(complytId -> complytId.matches(UUID_REGEXP.expression)
                                ? Mono.empty() : Mono.just(DtoErrorMessages.COMPLYT_ID_FORMAT_ERROR));

}
