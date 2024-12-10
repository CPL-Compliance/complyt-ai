package io.complyt.v1.validators;


import reactor.core.publisher.Flux;

public interface DtoBodyChecker<T> {
    Flux<String> check (T t);
}
