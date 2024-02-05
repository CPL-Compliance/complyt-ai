package com.complyt.v1.validators.body_checkers;

import reactor.core.publisher.Flux;

public interface DtoBodyChecker<T> {
    Flux<String> check (T t);
}
