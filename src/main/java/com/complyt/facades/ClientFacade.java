package com.complyt.facades;

import com.complyt.services.ClientService;
import com.complyt.services.OrderService;
import com.complyt.v1.model.ClientDto;
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

    public ClientDto createClient(@NonNull ClientDto clientDto) {
        if (clientDto.getOrders().size() > 0) {
            orderService.save(clientDto.getOrders());
        }

        return clientService.save(clientDto);
    }

    public ClientDto findByName(String name) {
        return clientService.findByName(name);
    }
}