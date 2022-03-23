package com.complyt.v1.mapper;

import com.complyt.domain.Client;
import com.complyt.v1.model.ClientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper( ClientMapper.class );

    Client clientDtoToClient(ClientDto clientDto);
    ClientDto clientToClientDto(Client client);
}