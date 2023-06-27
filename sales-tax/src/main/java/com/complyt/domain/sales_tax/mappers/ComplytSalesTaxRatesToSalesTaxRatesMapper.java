package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComplytSalesTaxRatesToSalesTaxRatesMapper {

    ComplytSalesTaxRatesToSalesTaxRatesMapper INSTANCE = Mappers.getMapper(ComplytSalesTaxRatesToSalesTaxRatesMapper.class);

    @Mapping(target = "combinedDistrictRate", source = "salesTaxRates.combinedDistrictRate")
    @Mapping(target = "cityRate", source = "salesTaxRates.cityRate")
    @Mapping(target = "taxRate", source = "salesTaxRates.taxRate")
    @Mapping(target = "countyRate", source = "salesTaxRates.countyRate")
    @Mapping(target = "stateRate", source = "salesTaxRates.stateRate")
    @Mapping(target = "ratesMetaData", source = "salesTaxRates.ratesMetaData")
    SalesTaxRates map(ComplytSalesTaxRates complytSalesTaxRates);
}