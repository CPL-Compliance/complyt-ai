package com.complyt.domain.mappers;

import com.complyt.annotations.Generated;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.taxjar.TaxJarData;
import com.taxjar.model.rates.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Generated // Not in Use
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TaxJarDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    TaxJarDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(TaxJarDataToSalesTaxRateMapper.class);

    @Mapping(target = "cityRate", expression = "java(toBigDecimal(rate.getCityRate()))")
    @Mapping(target = "taxRate", expression = "java(toBigDecimal(rate.getCombinedRate()))")
    @Mapping(target = "countyRate", expression = "java(toBigDecimal(rate.getCountyRate()))")
    @Mapping(target = "combinedDistrictRate", expression = "java(toBigDecimal(rate.getCombinedDistrictRate()))")
    @Mapping(target = "stateRate", expression = "java(toBigDecimal(rate.getStateRate()))")
    SalesTaxRates map(Rate rate);

    @Generated
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
