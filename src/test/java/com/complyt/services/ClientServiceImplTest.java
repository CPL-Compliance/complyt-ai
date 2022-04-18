package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Client;
import com.complyt.repositories.ClientRepository;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ClientDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientServiceImplTest {
    @InjectMocks
    public ClientServiceImpl clientServiceImpl;

    @Mock
    ClientRepository clientRepositoryMock;

    private Client client;

    private ClientDto clientDto;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String name = "Client";
        String city = "City";
        String country = "Country";
        String county = "County";
        String state = "State";
        String street = "Street";
        String zip = "Zip";

        AddressDto addressDto = new AddressDto(city, country, county, state, street, zip);
        Address address = new Address(city, country, county, state, street, zip);

        client = new Client(id, name, address, null);
        clientDto = new ClientDto(id, name, addressDto, null);
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