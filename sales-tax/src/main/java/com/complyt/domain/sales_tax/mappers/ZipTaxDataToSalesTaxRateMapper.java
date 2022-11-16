package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
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
    @Mapping(target = "countyDistrictRate", source = "districtSalesTax")
    @Mapping(target = "stateRate", source = "stateSalesTax")
    SalesTaxRate map(Result result);

    @Override
    default SalesTaxRate map(SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = ((ZipTaxData) salesTaxData);
        Result result = zipTaxData.getResults().get(0);
        SalesTaxRate salesTaxRate = map(result);

        return salesTaxRate;
    }
}
