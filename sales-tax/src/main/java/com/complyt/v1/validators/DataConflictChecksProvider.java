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

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataConflictChecksProvider<T> {

    Map<String, BiFunction<T, ServerRequest, Mono<Boolean>>> checksMap;

    public Mono<BiFunction<T, ServerRequest, Mono<Boolean>>> getCheck(@NonNull String pathVariable) {
        return Mono.just(checksMap.get(pathVariable));
    }
}
