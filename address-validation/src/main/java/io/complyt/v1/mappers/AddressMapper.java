package io.complyt.v1.mappers;

import io.complyt.domain.Address;
import io.complyt.v1.models.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    Address addressDtoToAddress(AddressDto addressDto);

    AddressDto addressToAddressDto(Address address);

}
