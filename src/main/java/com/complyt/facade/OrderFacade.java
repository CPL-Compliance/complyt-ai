package com.complyt.facade;

import com.complyt.domain.Order;
import com.complyt.service.ClientService;
import com.complyt.service.CustomerService;
import com.complyt.service.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderFacade {
    @Autowired
    CustomerService customerService;

    @Autowired
    ClientService clientService;

    @Autowired
    OrderService orderService;

    public void addOrderToClient(String client, @NotNull Order order) {
        customerService.save(order.getCustomer());
        orderService.save(order);
        clientService.addOrderToClient(client, order);
    }
}