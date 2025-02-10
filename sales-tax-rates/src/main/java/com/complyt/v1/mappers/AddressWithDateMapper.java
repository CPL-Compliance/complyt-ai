package com.complyt.v1.mappers;

import com.complyt.domain.AddressWithDate;
import com.complyt.v1.model.AddressWithDateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = {StringToLocalDateTimeMapper.class})
public interface AddressWithDateMapper {
    AddressWithDateMapper INSTANCE = Mappers.getMapper(AddressWithDateMapper.class);

    @Mapping(target = "effectiveDate", source = "effectiveDate", qualifiedByName = "parseLocalDateTimeToString")
    AddressWithDateDto addressWithDateToAddressDateDto(AddressWithDate addressWithTransactionDateDto);

    @Mapping(target = "effectiveDate", source = "effectiveDate", qualifiedByName = "parseStringToLocalDateTime")
    AddressWithDate addressWithDateDtoToAddressDate(AddressWithDateDto addressWithTransactionDateDto);
}
