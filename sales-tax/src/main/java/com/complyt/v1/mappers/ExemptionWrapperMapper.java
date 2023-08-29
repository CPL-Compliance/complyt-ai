package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ExemptionWrapperMapper {
    ExemptionWrapperMapper INSTANCE = Mappers.getMapper(ExemptionWrapperMapper.class);

    ExemptionWrapper exemptionWrapperDtoToExemptionWrapper(ExemptionWrapperDto exemptionWrapperDto);

    ExemptionWrapperDto exemptionWrapperToExemptionWrapperDto(ExemptionWrapper exemptionWrapper);
}