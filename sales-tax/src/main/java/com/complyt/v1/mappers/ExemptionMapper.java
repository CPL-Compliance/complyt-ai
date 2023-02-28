package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = {TimestampsMapper.class, ValidationDatsMapper.class})
public interface ExemptionMapper {
    ExemptionMapper INSTANCE = Mappers.getMapper(ExemptionMapper.class);

    Exemption exemptionDtoToExemption(ExemptionDto exemptionDto);

    ExemptionDto exemptionToExemptionDto(Exemption exemption);

}
