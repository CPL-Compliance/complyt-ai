package com.complyt.facades;

import com.complyt.domain.Order;
import com.complyt.services.ClientService;
import com.complyt.services.CustomerService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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

    public void addOrderToClient(String client, @NonNull Order order) {
//        customerService.save(order.getCustomer());
        orderService.save(order);
        clientService.addOrderToClient(client, order);
    }

    public Mono<Order> create(Order order) {
        return orderService.save(order);
    }
}