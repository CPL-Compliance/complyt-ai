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

    @Mapping(target = "ratesMetaData.cityDistrictRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.cityDistrictRate())))")
    @Mapping(target = "ratesMetaData.countyDistrictRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.countyDistrictRate())))")
    @Mapping(target = "ratesMetaData.specialDistrictRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.specialDistrictRate())))")
    @Mapping(target = "cityRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.cityRate())))")
    @Mapping(target = "taxRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.taxRate())))")
    @Mapping(target = "countyRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.countyRate())))")
    @Mapping(target = "stateRate", expression = "java(stripTrailingZeros(new BigDecimal(taxInfoItem.stateRate())))")
    @Mapping(target = "combinedDistrictRate", expression = "java(toCombinedDistrictRate(taxInfoItem))")
    SalesTaxRates map(TaxInfoItem taxInfoItem);

    default BigDecimal toCombinedDistrictRate(TaxInfoItem taxInfoItem) {
        return stripTrailingZeros(new BigDecimal(taxInfoItem.cityDistrictRate()).add(new BigDecimal(taxInfoItem.countyDistrictRate()).add(new BigDecimal(taxInfoItem.specialDistrictRate()))));
    }

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = ((FastTaxGetBestMatchData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);

        return map(taxInfoItem);
    }

    /**
     * Utility method to strip trailing zeros from a BigDecimal.
     */
    default BigDecimal stripTrailingZeros(BigDecimal value) {
        return value != null ? value.stripTrailingZeros() : null;
    }
}