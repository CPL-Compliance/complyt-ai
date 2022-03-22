package com.complyt.v1.mapper;

import com.complyt.domain.Client;
import com.complyt.v1.model.ClientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper( ClientMapper.class );

    @Mapping(source = "address", target = "address")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "orders", target = "orders")
    Client clientDtoToClient(ClientDto clientDto);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "orders", target = "orders")
    ClientDto clientToClientDto(Client client);
}