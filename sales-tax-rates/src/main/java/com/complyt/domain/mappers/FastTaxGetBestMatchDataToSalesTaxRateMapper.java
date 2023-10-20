package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FastTaxGetBestMatchDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    FastTaxGetBestMatchDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(FastTaxGetBestMatchDataToSalesTaxRateMapper.class);

    @Mapping(target = "ratesMetaData.cityDistrictRate", source = "cityDistrictRate")
    @Mapping(target = "ratesMetaData.countyDistrictRate", source = "countyDistrictRate")
    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "stateRate", source = "stateRate")
    @Mapping(expression = "java(toCombinedDistrictRate(taxInfoItem))", target = "combinedDistrictRate")
    SalesTaxRates map(TaxInfoItem taxInfoItem);

    default BigDecimal toCombinedDistrictRate(TaxInfoItem taxInfoItem) {
        return new BigDecimal(taxInfoItem.cityDistrictRate()).add(new BigDecimal(taxInfoItem.countyDistrictRate()));
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = ((FastTaxGetBestMatchData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);

        return map(taxInfoItem);
    }
}