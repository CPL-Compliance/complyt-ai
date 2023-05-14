package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesTaxDataToSalesTaxRateMapper {
    SalesTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper(SalesTaxDataToSalesTaxRateMapper.class);

    SalesTaxRates map(SalesTaxData salesTaxData);
}