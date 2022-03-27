package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.v1.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ClientMapperTest {

    @Test
    void clientToclientDto_ValidClient_ValidClientDto() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Name";
        Address address = new Address("City", "Country", "County", "State", "Street", "ZIP");
        List<Order> orders = null;
        Client client = new Client(id, name, address, orders);

        // When
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto(client);

        // Then
        assertThat(clientDto).isNotNull();
        assertThat(clientDto.getName()).isEqualTo(name);
        assertThat(clientDto.getAddress()).isEqualTo(address);
        assertThat(clientDto.getOrders()).isEqualTo(orders);
    }

    @Test
    void clientDtoToclient_ValidClientDto_ValidClient() {
        // Given
        String name = "Name";
        Address address = new Address("City", "Country", "County", "State", "Street", "ZIP");
        List<Order> orders = null;
        ClientDto clientDto = new ClientDto(name, address, orders);

        // When
        Client client = ClientMapper.INSTANCE.INSTANCE.clientDtoToClient(clientDto);

        // Then
        assertThat(client).isNotNull();
        assertThat(ObjectUtils.isEmpty(client.getId())).isEqualTo(true);
        assertThat(client.getName()).isEqualTo(name);
        assertThat(client.getOrders()).isEqualTo(orders);
        assertThat(client.getAddress()).isEqualTo(address);
    }

    @Test
    void clientDtoToclient_ClientDtoIsNull_ClientIsNull() {
        // Given
        ClientDto clientDto = null;

        // When
        Client client = ClientMapper.INSTANCE.clientDtoToClient(null);

        // Then
        assertThat(client).isNull();
    }

    @Test
    void clientToclientDto_ClientIsNull_ClientDtoIsNull() {
        // Given
        Client client = null;

        // When
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto(client);

        // Then
        assertThat(clientDto).isNull();
    }
}