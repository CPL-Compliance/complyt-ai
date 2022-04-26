package com.complyt.domain.sales_tax.mappers;


import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public  abstract class SalesTaxDataToSalesTaxRateMapper {
    public SalesTaxDataToSalesTaxRateMapper INSTANCE = Mappers.getMapper( SalesTaxDataToSalesTaxRateMapper.class );

    public abstract SalesTaxRate map(SalesTaxData salesTaxData);
}