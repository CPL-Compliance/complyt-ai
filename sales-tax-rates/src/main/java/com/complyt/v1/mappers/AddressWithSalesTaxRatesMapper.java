package com.complyt.v1.mappers;

import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.v1.model.AddressWithSalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface AddressWithSalesTaxRatesMapper {
    AddressWithSalesTaxRatesMapper INSTANCE = Mappers.getMapper(AddressWithSalesTaxRatesMapper.class);

    AddressWithSalesTaxRates addressWithSalesTaxRatesDtoToAddressWithSalesTaxRates(AddressWithSalesTaxRatesDto salesTaxRatesDto);

    AddressWithSalesTaxRatesDto addressWithSalesTaxRatesToAddressWithSalesTaxRatesDto(AddressWithSalesTaxRates addressWithSalesTaxRates);

}
