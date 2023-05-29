package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataConflictChecksProvider<T> {

    Function<T, Mono<Boolean>> bodyConflictCheckFunction;

    public Mono<Function<T, Mono<Boolean>>> getBodyConflictCheck() {
        return Mono.just(bodyConflictCheckFunction == null ? (body) -> Mono.just(true) : bodyConflictCheckFunction);
    }

}