package com.complyt.domain.mappers.address;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FastTaxDataToAddressMapper extends SalesTaxDataToAddressMapper {
    FastTaxDataToAddressMapper INSTANCE = Mappers.getMapper(FastTaxDataToAddressMapper.class);

    @Mapping(target = "city", source = "city")
    @Mapping(target = "county", source = "county")
    @Mapping(target = "state", source = "stateAbbreviation")
    @Mapping(target = "zip", source = "zip")
    Address map(TaxInfoItem taxInfoItem);

    @Override
    default Address map(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = ((FastTaxData) salesTaxData);
        TaxInfoItem taxInfoItem = fastTaxData.getTaxInfoItems().get(0);

        return map(taxInfoItem);
    }

}