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

    Function<T, Mono<String>> bodyConflictCheckFunction;

    Map<String, BiFunction<T, ServerRequest, Mono<String>>> pathVariablesChecksMap;

    public Mono<BiFunction<T, ServerRequest, Mono<String>>> getPathVariableCheck(@NonNull String pathVariable) {
        BiFunction<T, ServerRequest, Mono<String>> check = pathVariablesChecksMap.get(pathVariable);
        return Mono.just(check == null ? (body, request) -> Mono.empty() : check);
    }

    public Mono<Function<T, Mono<String>>> getBodyConflictCheck() {
        return Mono.just(bodyConflictCheckFunction == null ? (body) -> Mono.empty(): bodyConflictCheckFunction);
    }

}