package com.complyt.services.crud;

import lombok.NonNull;
import reactor.core.publisher.Flux;

public interface FindByName <T> {
    Flux<T> findByName(@NonNull final String name);
}
