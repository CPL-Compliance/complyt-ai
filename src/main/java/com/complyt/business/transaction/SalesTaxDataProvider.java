package com.complyt.business.transaction;

import reactor.core.publisher.Mono;

public interface SalesTaxDataProvider<T> {
    Mono<T> provide(T t);
}
