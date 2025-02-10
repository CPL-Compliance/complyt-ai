package io.complyt.domain.mappers;

import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CachedAddressDataToAddressMapper {
    CachedAddressDataToAddressMapper INSTANCE = Mappers.getMapper(CachedAddressDataToAddressMapper.class);

    @Mapping(source = "address.country", target = "country")
    @Mapping(source = "address.state", target = "state")
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.county", target = "county")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.zip", target = "zip")
    Address map(CachedAddressData cachedAddressData);
}
