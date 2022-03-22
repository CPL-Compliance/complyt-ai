package com.complyt.v1.mapper;

import com.complyt.domain.Address;
import com.complyt.domain.Client;
import com.complyt.domain.Order;
import com.complyt.v1.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

    @Test
    void clientToclientDto_ValidClient_ValidClientDto() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Name";
        Address address = null;
        List<Order> orders = null;
        Client client = new Client(id, name, address, orders);

        // When
        ClientDto clientDto = ClientMapper.INSTANCE.clientToClientDto( client );

        // Then
        assertThat( clientDto.getName() ).isEqualTo( name );
    }

    @Test
    void clientDtoToclient_ValidClientDto_ValidClient() {

    }
}