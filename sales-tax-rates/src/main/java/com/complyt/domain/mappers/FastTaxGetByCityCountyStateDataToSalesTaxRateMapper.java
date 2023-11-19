package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FastTaxGetByCityCountyStateDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {

    FastTaxGetByCityCountyStateDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.class);

    @Mapping(target = "ratesMetaData.cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "ratesMetaData.countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "totalTaxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "stateRate", source = "stateRate")
    @Mapping(expression = "java(toCombinedDistrictRate(fastTaxGetByCityCountyStateData))", target = "combinedDistrictRate")
    SalesTaxRates map(FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData);

    default BigDecimal toCombinedDistrictRate(FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData) {
        return new BigDecimal(fastTaxGetByCityCountyStateData.getCityDistrictRate()).add(new BigDecimal(fastTaxGetByCityCountyStateData.getCountyDistrictRate()));
    }

}