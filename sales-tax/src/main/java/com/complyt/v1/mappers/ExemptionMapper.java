package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ExemptionMapper {
    ExemptionMapper INSTANCE = Mappers.getMapper(ExemptionMapper.class);

    Exemption exemptionDtoToExemption(ExemptionDto exemptionDto);

    ExemptionDto exemptionToExemptionDto(Exemption exemption);
    ComplytTimestamp map(ComplytTimestampDto value);

    ComplytTimestampDto map(ComplytTimestamp value);
}
