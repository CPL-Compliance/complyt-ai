package com.complyt.services;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.repositories.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ClientRepository clientRepository;

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client findOneByName(String name) {
        return clientRepository.findOneByName(name);
    }

    @Override
    public List<Client> findByName(String name) {
        return clientRepository.findByName(name);
    }

    @Override
    public Client findById(String id) {
        return clientRepository.findOneById(id);
    }

    @Override
    public List<Client> findAll() {
        return null;
    }

    @Override
    public void addOrderToClient(String name, Order order) {
        clientRepository.addOrderToClient(name, order);
    }
}