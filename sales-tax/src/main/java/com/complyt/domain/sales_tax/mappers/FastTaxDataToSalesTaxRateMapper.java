package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FastTaxDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    FastTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxDataToSalesTaxRateMapper.class);

    @Mapping(target = "cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "stateRate", source = "stateRate")
    SalesTaxRates map(TaxInfoItem taxInfoItem);

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = ((FastTaxData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxData.getTaxInfoItems().get(0);
        SalesTaxRates salesTaxRates = map(taxInfoItem);

        return salesTaxRates;
    }
}
