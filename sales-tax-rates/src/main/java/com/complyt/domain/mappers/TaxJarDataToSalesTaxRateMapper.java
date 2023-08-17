package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.taxjar.TaxJarData;
import com.taxjar.model.rates.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TaxJarDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    TaxJarDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(TaxJarDataToSalesTaxRateMapper.class);

    @Mapping(expression = "java(toBigDecimal(rate.getCityRate()))", target = "cityRate")
    @Mapping(expression = "java(toBigDecimal(rate.getCombinedRate()))", target = "taxRate")
    @Mapping(expression = "java(toBigDecimal(rate.getCountyRate()))", target = "countyRate")
    @Mapping(expression = "java(toBigDecimal(rate.getCombinedDistrictRate()))", target = "combinedDistrictRate")
    @Mapping(expression = "java(toBigDecimal(rate.getStateRate()))", target = "stateRate")
    SalesTaxRates map(Rate rate);

    default BigDecimal toBigDecimal(Float rate) {
        if (rate == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(rate);
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        TaxJarData taxJarData = ((TaxJarData) salesTaxData);
        Rate rate = taxJarData.getRate();

        return map(rate);
    }
}
