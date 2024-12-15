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

    @Mapping(source = "country", target = "country")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "county", target = "county")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "zip", target = "zip")
    Address map(CachedAddressData cachedAddressData);
}
