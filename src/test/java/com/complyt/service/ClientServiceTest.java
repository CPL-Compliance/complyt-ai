package com.complyt.service;

import com.complyt.domain.Address;
import com.complyt.domain.Client;
import com.complyt.domain.State;
import com.complyt.repository.ClientRepository;
import com.complyt.repository.StateRepository;
import com.complyt.v1.model.ClientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientServiceTest {
    @InjectMocks
    public ClientService clientService;

    @Mock
    ClientRepository clientRepositoryMock;

    private Client client;

    private ClientDto clientDto;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String name = "Client";
        String city = "City";
        String country = "Country";
        String county = "County";
        String state = "State";
        String street = "Street";
        String zip = "Zip";

        Address address = new Address(city, country, county, state, street, zip);
        client = new Client(id, name, address, null);

        clientDto = new ClientDto(name, address, null);
    }

    @Test
    void save_ValidClient_ClientDto() {
        //when(clientRepositoryMock.save(client)).thenReturn(client);

        //clientService.save(clientDto);
    }

    @Test
    void getClient() {

    }

    @Test
    void addOrderToClient() {
    }
}