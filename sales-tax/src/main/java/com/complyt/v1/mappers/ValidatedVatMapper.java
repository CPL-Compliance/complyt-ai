package com.complyt.v1.mappers;

import com.complyt.domain.ValidatedVat;
import com.complyt.v1.models.vat_validation.ValidatedVatDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = {TimestampsMapper.class})
public interface ValidatedVatMapper {
    ValidatedVatMapper INSTANCE = Mappers.getMapper(ValidatedVatMapper.class);

    ValidatedVat validatedVatDtoToValidatedVat(ValidatedVatDto validatedVatDto);

    ValidatedVatDto validatedVatToValidatedVatDto(ValidatedVat validatedVat);
}
