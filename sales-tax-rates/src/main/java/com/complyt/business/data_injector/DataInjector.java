package com.complyt.business.data_injector;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface DataInjector<V, U> {

    Mono<V> inject(@NonNull U u);
}
