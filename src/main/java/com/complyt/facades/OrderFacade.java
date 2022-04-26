package com.complyt.facades;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.services.ClientService;
import com.complyt.services.CustomerService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class OrderFacade {
    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;

    @Qualifier("clientServiceImpl")
    @NonNull
    private ClientService clientService;

    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    public Mono<Order> save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> upsert(Order order) {
        return orderService.upsert(order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }
}
