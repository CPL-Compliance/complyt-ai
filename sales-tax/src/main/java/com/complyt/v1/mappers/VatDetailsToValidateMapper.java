package com.complyt.v1.mappers;

import com.complyt.domain.VatDetailsToValidate;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface VatDetailsToValidateMapper {
    VatDetailsToValidateMapper INSTANCE = Mappers.getMapper(VatDetailsToValidateMapper.class);


    VatDetailsToValidate vatDetailsToValidateDtoToVatDetailsToValidate(VatDetailsToValidateDto vatDetailsToValidateDto);

    VatDetailsToValidateDto vatDetailsToValidateToVatDetailsToValidateDto(VatDetailsToValidate validatedVat);
}
