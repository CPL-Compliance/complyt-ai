package io.complyt.v1.mappers;

import io.complyt.domain.CachedAddressData;
import io.complyt.v1.models.CachedAddressDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CachedAddressDataMapper {
    CachedAddressDataMapper INSTANCE = Mappers.getMapper(CachedAddressDataMapper.class);

    CachedAddressDataDto cachedAddressDataToCachedAddressDataDto(CachedAddressData cachedAddressData);
}
