package com.complyt.v1.mappers;

import com.complyt.domain.gt.GtAddress;
import com.complyt.v1.model.gt.GtAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface GtAddressMapper {
    GtAddressMapper INSTANCE = Mappers.getMapper(GtAddressMapper.class);

    GtAddress gtAddressDtoToGtAddress(GtAddressDto gstAddressDto);

    GtAddressDto addressToAddressDto(GtAddress gstAddress);

}