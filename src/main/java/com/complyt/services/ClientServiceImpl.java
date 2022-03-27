package com.complyt.services;

import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.repositories.ClientRepository;
import com.complyt.v1.mappers.ClientMapper;
import com.complyt.v1.model.ClientDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ClientRepository clientRepository;

    public ClientDto save(ClientDto clientDto) {
        Client client = ClientMapper.INSTANCE.clientDtoToClient(clientDto);
        Client result = clientRepository.save(client);

        return ClientMapper.INSTANCE.clientToClientDto(result);
    }

    public ClientDto findByName(String name) {
        Client client = clientRepository.findOne(name);

        return ClientMapper.INSTANCE.clientToClientDto(client);
    }

    public void addOrderToClient(String name, Order order) {
        clientRepository.addOrderToClient(name, order);
    }
}