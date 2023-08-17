package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ZipTaxDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    ZipTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(ZipTaxDataToSalesTaxRateMapper.class);

    @Mapping(expression = "java(toBigDecimal(result.districtSalesTax()))",target = "ratesMetaData.cityDistrictRate")
    @Mapping(expression = "java(toBigDecimal(result.district5SalesTax()))",target = "ratesMetaData.countyDistrictRate")
    @Mapping(expression = "java(toBigDecimal(result.citySalesTax()))",target = "cityRate")
    @Mapping(expression = "java(toBigDecimal(result.taxSales()))",target = "taxRate")
    @Mapping(expression = "java(toBigDecimal(result.countySalesTax()))",target = "countyRate")
    @Mapping(expression = "java(toBigDecimal(result.stateSalesTax()))",target = "stateRate")
    @Mapping(expression = "java(toCombinedDistrictRate(result))", target = "combinedDistrictRate")
    SalesTaxRates map(Result result);

    default BigDecimal toBigDecimal(double rate) {
        return new BigDecimal(rate);
    }

    default BigDecimal toCombinedDistrictRate(Result result) {
        return new BigDecimal(result.districtSalesTax()).add(new BigDecimal(result.district5SalesTax()));
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = ((ZipTaxData) salesTaxData);
        Result result = zipTaxData.getResults().get(0);

        return map(result);
    }
}