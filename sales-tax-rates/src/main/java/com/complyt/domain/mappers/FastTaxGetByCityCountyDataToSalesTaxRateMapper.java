package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FastTaxGetByCityCountyDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {

    FastTaxGetByCityCountyDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxGetByCityCountyDataToSalesTaxRateMapper.class);

    @Mapping(target = "ratesMetaData.cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "ratesMetaData.countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "totalTaxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "stateRate", source = "stateRate")
    @Mapping(expression = "java(toCombinedDistrictRate(fastTaxGetByCityCountyData))", target = "combinedDistrictRate")
    SalesTaxRates map(FastTaxGetByCityCountyData fastTaxGetByCityCountyData);

    default BigDecimal toCombinedDistrictRate(FastTaxGetByCityCountyData fastTaxGetByCityCountyData) {
        return new BigDecimal(fastTaxGetByCityCountyData.getCityDistrictRate()).add(new BigDecimal(fastTaxGetByCityCountyData.getCountyDistrictRate()));
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        FastTaxGetByCityCountyData fastTaxGetByCityCountyData = ((FastTaxGetByCityCountyData) salesTaxData);

        return map(fastTaxGetByCityCountyData);
    }
}