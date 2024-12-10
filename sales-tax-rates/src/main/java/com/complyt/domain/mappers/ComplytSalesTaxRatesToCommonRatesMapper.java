package com.complyt.domain.mappers;

import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ComplytSalesTaxRatesToCommonRatesMapper {

    ComplytSalesTaxRatesToCommonRatesMapper INSTANCE = Mappers.getMapper(ComplytSalesTaxRatesToCommonRatesMapper.class);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "salesTaxRates", source = "salesTaxRates")
    @Mapping(target = "source", expression = "java(SalesTaxSources.SERVICE_OBJECT)")
    CommonSalesTaxRates map(ComplytSalesTaxRates complytSalesTaxRates);

}
