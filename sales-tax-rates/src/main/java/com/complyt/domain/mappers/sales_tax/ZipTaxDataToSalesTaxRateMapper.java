package com.complyt.domain.mappers.sales_tax;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ZipTaxDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    ZipTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(ZipTaxDataToSalesTaxRateMapper.class);

    @Mapping(target = "cityDistrictRate", source = "districtSalesTax")
    @Mapping(target = "cityRate", source = "citySalesTax")
    @Mapping(target = "taxRate", source = "taxSales")
    @Mapping(target = "countyRate", source = "countySalesTax")
    @Mapping(target = "countyDistrictRate", source = "district5SalesTax")
    @Mapping(target = "stateRate", source = "stateSalesTax")
    SalesTaxRates map(Result result);

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = ((ZipTaxData) salesTaxData);
        Result result = zipTaxData.getResults().get(0);

        return map(result);
    }
}