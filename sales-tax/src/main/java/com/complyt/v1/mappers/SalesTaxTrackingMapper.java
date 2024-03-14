package com.complyt.v1.mappers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.v1.models.SalesTaxTrackingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, imports = HashMap.class,
        uses = {TimestampsMapper.class})
public interface SalesTaxTrackingMapper {
    SalesTaxTrackingMapper INSTANCE = Mappers.getMapper(SalesTaxTrackingMapper.class);
    @Mapping(target = "transactionNexusSummaries", expression = "java(new HashMap<>())")
    @Mapping(target = "nexusCalculationSummaries", source = "nexusCalculationSummaries", defaultExpression = "java(new HashMap<>())")
    SalesTaxTracking salesTaxTrackingDtoToSalesTaxTracking(SalesTaxTrackingDto salesTaxTrackingDto);

    SalesTaxTrackingDto salesTaxTrackingToSalesTaxTrackingDto(SalesTaxTracking salesTaxTracking);
}
