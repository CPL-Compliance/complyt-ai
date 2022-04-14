package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.Client;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ClientDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ClientDtoMapperTest {

    @Test
    void clientToclientDto_ValidClient_ValidClientDto() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Name";
        Address address = new Address("City", "Country", "County", "State", "Street", "ZIP");
        List<ObjectId> orders = null;
        Client client = new Client(id, name, address, orders);

        // When
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto(client);

        // Then
        assertThat(clientDto).isNotNull();
        assertThat(clientDto.getName()).isEqualTo(name);
        assertThat(clientDto.getAddress()).isEqualTo(address);
        assertThat(clientDto.getOrdersId()).isEqualTo(orders);
    }

    @Test
    void clientDtoToclient_ValidClientDto_ValidClient() {
        // Given
        String name = "Name";
        AddressDto AddressDto = new AddressDto("City", "Country", "County", "State", "Street", "ZIP");
        List<ObjectId> orders = null;
        ClientDto clientDto = new ClientDto();
        clientDto.setAddress(AddressDto);
        clientDto.setName(name);
        clientDto.setOrdersId(orders);

        // When
        com.complyt.domain.Client client = ClientMapper.INSTANCE.INSTANCE.clientDtoToClient(clientDto);

        // Then
        assertThat(client).isNotNull();
        assertThat(ObjectUtils.isEmpty(client.getId())).isEqualTo(true);
        assertThat(client.getName()).isEqualTo(name);
        assertThat(client.getOrdersId()).isEqualTo(orders);
        assertThat(client.getAddress()).isEqualTo(AddressDto);
    }

    @Test
    void clientDtoToclient_ClientDtoIsNull_ClientIsNull() {
        // Given
        ClientDto clientDto = null;

        // When
        com.complyt.domain.Client client = ClientMapper.INSTANCE.clientDtoToClient(null);

        // Then
        assertThat(client).isNull();
    }

    @Test
    void clientToclientDto_ClientIsNull_ClientDtoIsNull() {
        // Given
        com.complyt.domain.Client client = null;

        // When
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto(client);

        // Then
        assertThat(clientDto).isNull();
    }
}