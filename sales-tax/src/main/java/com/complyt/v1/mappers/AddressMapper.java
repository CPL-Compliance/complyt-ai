package com.complyt.v1.mappers;

import com.complyt.domain.transaction.Address;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    MandatoryAddressDto addressToMandatoryAddressDto(Address address);

    Address mandatoryAddressDtoToAddress(MandatoryAddressDto mandatoryAddressDto);
}