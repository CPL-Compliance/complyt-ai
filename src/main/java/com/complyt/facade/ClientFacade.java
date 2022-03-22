package com.complyt.facade;

import com.complyt.domain.Client;
import com.complyt.service.ClientService;
import com.complyt.service.OrderService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientFacade {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ClientService clientService;

    public Client createClient(@NotNull Client client){
        if(client.getOrders() != null && client.getOrders().size() > 0){
            orderService.save(client.getOrders());
        }

        return clientService.save(client);
    }

    public Client getClient(String name){
        return clientService.getClient(name);
    }
}
