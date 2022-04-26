package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public abstract class FastTaxDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    public static FastTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxDataToSalesTaxRateMapper.class);

    @Mapping(target = "cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "stateRate", source = "stateRate")
    public abstract SalesTaxRate map(TaxInfoItem taxInfoItem);

    public SalesTaxRate map(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = ((FastTaxData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxData.getTaxInfoItems().get(0);
        SalesTaxRate salesTaxRate = map(taxInfoItem);

        return salesTaxRate;
    }
}
