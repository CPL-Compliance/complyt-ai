package com.complyt.v1.mappers;

import com.complyt.business.tax.sales_tax.models.ComplytInternalSalesTaxRatesDto;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComplytSalesTaxRatesMapper {
    ComplytSalesTaxRatesMapper INSTANCE = Mappers.getMapper(ComplytSalesTaxRatesMapper.class);

    ComplytSalesTaxRates complytSalesTaxRatesDtoToComplytSalesTaxRates(ComplytInternalSalesTaxRatesDto complytSalesTaxRatesDto);
    ComplytInternalSalesTaxRatesDto complytSalesTaxRatesToComplytSalesTaxRatesDto(ComplytSalesTaxRates complytSalesTaxRates);
}
