package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FastTaxDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    FastTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxDataToSalesTaxRateMapper.class);

    @Mapping(target = "ratesMetaData.cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "ratesMetaData.countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "stateRate", source = "stateRate")
    @Mapping(expression = "java(toCombinedDistrictRate(taxInfoItem))", target = "combinedDistrictRate")
    SalesTaxRates map(TaxInfoItem taxInfoItem);

    default double toCombinedDistrictRate(TaxInfoItem taxInfoItem) {
        return Double.parseDouble(taxInfoItem.cityDistrictRate()) + Double.parseDouble(taxInfoItem.countyDistrictRate());
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = ((FastTaxData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxData.getTaxInfoItems().get(0);

        return map(taxInfoItem);
    }
}