package com.complyt.v1.mappers;

import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ComplytSalesTaxRatesMapper {
    ComplytSalesTaxRatesMapper INSTANCE = Mappers.getMapper(ComplytSalesTaxRatesMapper.class);

    ComplytSalesTaxRates complytSalesTaxRatesDtoToComplytSalesTaxRates(ComplytSalesTaxRatesDto salesTaxRatesDto);

    ComplytSalesTaxRatesDto complytSalesTaxRatesToComplytSalesTaxRates(ComplytSalesTaxRates addressWithSalesTaxRates);

}
