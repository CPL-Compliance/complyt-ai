package com.complyt.facade;

import com.complyt.service.ClientService;
import com.complyt.service.OrderService;
import com.complyt.v1.model.ClientDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientFacade {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ClientService clientService;

    public ClientDto createClient(@NotNull ClientDto clientDto) {
        if (clientDto.getOrders() != null && clientDto.getOrders().size() > 0) {
            orderService.save(clientDto.getOrders());
        }

        return clientService.save(clientDto);
    }

    public ClientDto getClient(String name) {
        return clientService.getClient(name);
    }
}