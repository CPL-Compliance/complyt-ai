package com.complyt.v1.mappers;

import com.complyt.domain.SalesTaxRatesData;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface SalesTaxRatesDataMapper {

    SalesTaxRatesDataMapper INSTANCE = Mappers.getMapper(SalesTaxRatesDataMapper.class);

    SalesTaxRatesDataDto salesTaxRatesDataTosalesTaxRatesDataDto(SalesTaxRatesData salesTaxRatesData);
}
