package com.complyt.v1.mappers;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface NexusCalculationSummaryMapper {

    NexusCalculationSummaryMapper INSTANCE = Mappers.getMapper(NexusCalculationSummaryMapper.class);

    NexusCalculationSummary nexusCalculationSummaryDtoToNexusCalculationSummary(NexusCalculationSummaryDto nexusCalculationSummaryDto);

    NexusCalculationSummaryDto nexusCalculationSummaryToNexusCalculationSummaryDto(NexusCalculationSummary nexusCalculationSummary);

}