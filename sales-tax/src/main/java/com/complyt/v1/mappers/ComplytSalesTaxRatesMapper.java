package com.complyt.v1.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.v1.models.sales_tax.ComplytSalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComplytSalesTaxRatesMapper {
    ComplytSalesTaxRatesMapper INSTANCE = Mappers.getMapper(ComplytSalesTaxRatesMapper.class);

    ComplytSalesTaxRates complytSalesTaxRatesDtoToComplytSalesTaxRates(ComplytSalesTaxRatesDto complytSalesTaxRatesDto);
    ComplytSalesTaxRatesDto complytSalesTaxRatesToComplytSalesTaxRatesDto(ComplytSalesTaxRates complytSalesTaxRates);

}
