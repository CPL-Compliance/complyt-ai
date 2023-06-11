package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataConflictChecksProvider<T> {

    Function<T, Mono<Boolean>> bodyConflictCheckFunction;

    Map<String, BiFunction<T, ServerRequest, Mono<Boolean>>> pathVariablesChecksMap;

    public Mono<BiFunction<T, ServerRequest, Mono<Boolean>>> getPathVariableCheck(@NonNull String pathVariable) {
        BiFunction<T, ServerRequest, Mono<Boolean>> check = pathVariablesChecksMap.get(pathVariable);
        return Mono.just(check == null ? (body, request) -> Mono.just(true) : check);
    }

    public Mono<Function<T, Mono<Boolean>>> getBodyConflictCheck() {
        return Mono.just(bodyConflictCheckFunction == null ? (body) -> Mono.just(true) : bodyConflictCheckFunction);
    }

}