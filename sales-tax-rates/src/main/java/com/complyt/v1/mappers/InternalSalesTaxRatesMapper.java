package com.complyt.v1.mappers;

import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface InternalSalesTaxRatesMapper {
    InternalSalesTaxRatesMapper INSTANCE = Mappers.getMapper(InternalSalesTaxRatesMapper.class);

    InternalSalesTaxRates internalRatesDtoToInternalRates(InternalSalesTaxRatesDto internalSalesTaxRatesDto);

    InternalSalesTaxRatesDto internalRatesToInternalRatesDto(InternalSalesTaxRates internalSalesTaxRates);
}
