package com.complyt.v1.mappers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.v1.models.SalesTaxTrackingDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface SalesTaxTrackingMapper {
    SalesTaxTrackingMapper INSTANCE = Mappers.getMapper(SalesTaxTrackingMapper.class);

    SalesTaxTracking salesTaxTrackingDtoToSalesTaxTracking(SalesTaxTrackingDto salesTaxTrackingDto);

    SalesTaxTrackingDto salesTaxTrackingToSalesTaxTrackingDto(SalesTaxTracking salesTaxTracking);

}
