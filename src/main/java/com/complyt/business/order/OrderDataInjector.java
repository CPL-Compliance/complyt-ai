package com.complyt.business.order;

import com.complyt.domain.Order;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface OrderDataInjector<T> {
    Mono<Order> act(Map<String,T> t);
}