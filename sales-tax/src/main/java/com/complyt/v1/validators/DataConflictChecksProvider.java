package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataConflictChecksProvider<T> {

    Map<String, BiFunction<T, ServerRequest, Mono<Boolean>>> pathVariablesChecksMap;
    Map<Set<String>, Function<T, Mono<Boolean>>> bodyChecksMap;

    public Mono<BiFunction<T, ServerRequest, Mono<Boolean>>> getPathVariableCheck(@NonNull String pathVariable) {
        BiFunction<T, ServerRequest, Mono<Boolean>> check = pathVariablesChecksMap.get(pathVariable);
        return Mono.just(check == null ? (body, request) -> Mono.just(true) : check);
    }

    public Mono<Function<T, Mono<Boolean>>> getBodyCheck(@NonNull Set<String> variableList) {
        Function<T, Mono<Boolean>> check = bodyChecksMap.get(variableList);
        return Mono.just(check == null ? (body) -> Mono.just(true) : check);
    }
}
