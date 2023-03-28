package com.complyt.v1.mappers;

import com.complyt.domain.SalesTaxRates;
import com.complyt.v1.model.SalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface SalesTaxRatesMapper {
    SalesTaxRatesMapper INSTANCE = Mappers.getMapper(SalesTaxRatesMapper.class);

    SalesTaxRates salesTaxRatesDtoToSalesTaxRates(SalesTaxRatesDto salesTaxRatesDto);

    SalesTaxRatesDto salesTaxRatesToSalesTaxRatesDto(SalesTaxRates salesTaxRates);

}