package com.complyt.business.order;

import com.complyt.domain.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderDataInjector<T> {
    Mono<Order> act(Flux<T> t);
}