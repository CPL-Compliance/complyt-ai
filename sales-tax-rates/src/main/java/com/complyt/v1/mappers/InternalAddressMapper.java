package com.complyt.v1.mappers;

import com.complyt.domain.internal_rates.InternalAddress;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface InternalAddressMapper {
    InternalAddressMapper INSTANCE = Mappers.getMapper(InternalAddressMapper.class);

    InternalAddress internalAddressDtoToInternalAddress(InternalAddressDto internalAddressDto);

    InternalAddressDto internalSalesTaxRatesToInternalSalesTaxRatesDto(InternalAddress internalAddress);

}
