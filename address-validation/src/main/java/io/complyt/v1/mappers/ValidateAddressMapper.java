package io.complyt.v1.mappers;

import io.complyt.domain.Address;
import io.complyt.domain.ValidatedAddress;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.models.ValidatedAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ValidateAddressMapper {
    ValidateAddressMapper INSTANCE = Mappers.getMapper(ValidateAddressMapper.class);

    ValidatedAddress validatedAddressDtoToAddress(ValidatedAddressDto validatedAddressDto);

    ValidatedAddressDto validatedAddressToValidatedAddressDto(ValidatedAddress validatedAddress);
}
