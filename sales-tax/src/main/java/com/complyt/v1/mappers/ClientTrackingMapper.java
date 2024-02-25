package com.complyt.v1.mappers;

import com.complyt.domain.ClientTracking;
import com.complyt.v1.models.ClientTrackingDtoTenant;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, imports = HashMap.class, uses = {TimestampsMapper.class})
public interface ClientTrackingMapper {
    ClientTrackingMapper INSTANCE = Mappers.getMapper(ClientTrackingMapper.class);

    ClientTracking clientTrackingDtoTenantToClientTracking(ClientTrackingDtoTenant ClientTrackingDtoTenant);
    ClientTrackingDtoTenant clientTrackingToClientTrackingDtoTenant(ClientTracking clientTracking);
}
