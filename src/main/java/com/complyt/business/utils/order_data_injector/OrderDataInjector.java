package com.complyt.business.utils.order_data_injector;

import com.complyt.domain.Order;
import reactor.core.publisher.Mono;

public interface OrderDataInjector<T> {
    Mono<Order> inject(T t);
}
