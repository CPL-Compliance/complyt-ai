package com.complyt.domain.mappers.address;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesTaxDataToAddressMapper {
    SalesTaxDataToAddressMapper INSTANCE = Mappers.getMapper(SalesTaxDataToAddressMapper.class);

    Address map(SalesTaxData salesTaxData);

}
