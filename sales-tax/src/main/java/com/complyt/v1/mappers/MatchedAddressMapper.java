package com.complyt.v1.mappers;

import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface MatchedAddressMapper {

    MatchedAddressMapper INSTANCE = Mappers.getMapper(MatchedAddressMapper.class);

    MatchedAddressData matchedAddressDataDtoToMatchedAddress(MatchedAddressDataDto matchedAddressDataDto);

    MatchedAddressDataDto matchedAddressDataToMatchedAddressDto(MatchedAddressData matchedAddressData);
}
