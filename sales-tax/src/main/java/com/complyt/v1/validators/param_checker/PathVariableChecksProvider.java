package com.complyt.v1.validators.param_checker;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PathVariableChecksProvider {
    Map<String, Function<ServerRequest, Mono<String>>> pathVariablesChecksMap;

    public Mono<Function<ServerRequest, Mono<String>>> getPathVariableCheck(@NonNull String pathVariable) {
        Function<ServerRequest, Mono<String>> check = pathVariablesChecksMap.get(pathVariable);
        return Mono.just(check == null ? (param) -> Mono.empty() : check);
    }


}
