package com.complyt.v1.mappers;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface MatchedAddressMapper {

    MatchedAddressMapper INSTANCE = Mappers.getMapper(MatchedAddressMapper.class);

    MatchedAddressDataDto matchedAddressDataToMatchedAddressDto(MatchedAddressData matchedAddressData);

    MatchedAddressData matchedAddressDataDtoToMatchedAddress(MatchedAddressDataDto matchedAddressDataDto);
}