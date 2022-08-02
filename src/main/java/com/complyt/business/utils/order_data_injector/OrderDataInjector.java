package com.complyt.business.utils.order_data_injector;

import com.complyt.domain.Order;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OrderDataInjector<T> {
    Mono<Order> inject(Map<String, T> t);
}
