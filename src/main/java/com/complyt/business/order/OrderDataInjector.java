package com.complyt.business.order;

import com.complyt.domain.Order;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderDataInjector<T> {
    Mono<Order> act(List<T> t);
}