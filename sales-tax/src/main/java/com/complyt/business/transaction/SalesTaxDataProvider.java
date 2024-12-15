package com.complyt.business.transaction;

import reactor.core.publisher.Mono;

public interface SalesTaxDataProvider<T, U> {
    Mono<T> provide(T t, U u);
}
