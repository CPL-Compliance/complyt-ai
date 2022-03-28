package com.complyt.facades;

import com.complyt.domain.Client;
import com.complyt.services.ClientService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClientFacade {

    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("clientServiceImpl")
    @NonNull
    private ClientService clientService;

    public Client createClient(@NonNull Client client) {
        if (client.getOrders().size() > 0) {
            orderService.save(client.getOrders());
        }

        return clientService.save(client);
    }

    public Client findByName(String name) {
        return clientService.findOneByName(name);
    }
}