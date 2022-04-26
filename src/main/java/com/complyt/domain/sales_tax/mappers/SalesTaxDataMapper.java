package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;


public class SalesTaxDataMapper {

    SalesTaxDataMapper INSTANCE = Mappers.getMapper( SalesTaxDataMapper.class );

    @Profile({"fastTax","default"})
    @Bean("salesTaxDataMapper")
    public SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        TaxInfoItem taxInfoItem = fastTaxData.getTaxInfoItems().get(0);
        String cityDistrictRate = taxInfoItem.getCityDistrictRate();
        String cityRate = taxInfoItem.getCityRate();
        String countyDistrictRate = taxInfoItem.getCountyDistrictRate();
        String countyRate = taxInfoItem.getCountyRate();
        String stateRate = taxInfoItem.getStateRate();
        String taxRate = taxInfoItem.getTaxRate();
        return new SalesTaxRate(cityDistrictRate,cityRate,countyRate,countyDistrictRate,stateRate,taxRate);
    }

        @Profile({"zipTax"})
        @Bean("salesTaxDataMapper")
        public SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData) {
            return null;
        }
}