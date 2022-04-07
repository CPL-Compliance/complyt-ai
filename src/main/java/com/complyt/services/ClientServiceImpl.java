package com.complyt.services;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    @Override
    public Mono<Client> save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Mono<Client> findOneByName(String name) {
        return clientRepository.findOneByName(name);
    }

    @Override
    public Flux<Client> findByName(String name) {
        return clientRepository.findByName(name);
    }

    @Override
    public Mono<Client> findById(String id) {
        return clientRepository.findOneById(id);
    }

    @Override
    public Flux<Client> findAll() {
        return null;
    }

    @Override
    public void addOrderToClient(String name, Order order) {
        clientRepository.addOrderToClient(name, order);
    }
}