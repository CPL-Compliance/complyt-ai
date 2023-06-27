package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.taxjar.TaxJarData;
import com.taxjar.model.rates.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TaxJarDataToSalesTaxRateMapper extends SalesTaxDataToSalesTaxRateMapper {
    TaxJarDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(TaxJarDataToSalesTaxRateMapper.class);

    @Mapping(target = "cityRate", source = "cityRate")
    @Mapping(target = "taxRate", source = "combinedRate")
    @Mapping(target = "countyRate", source = "countyRate")
    @Mapping(target = "combinedDistrictRate", source = "combinedDistrictRate")
    @Mapping(target = "stateRate", source = "stateRate")
    SalesTaxRates map(Rate rate);

    @Override
    default SalesTaxRates map(SalesTaxData salesTaxData) {
        TaxJarData taxJarData = ((TaxJarData) salesTaxData);
        Rate rate = taxJarData.getRate();

        return map(rate);
    }
}
