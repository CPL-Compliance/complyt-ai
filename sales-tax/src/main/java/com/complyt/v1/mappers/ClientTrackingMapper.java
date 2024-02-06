package com.complyt.v1.mappers;

import com.complyt.domain.ClientTracking;
import com.complyt.v1.models.ClientTrackingDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, imports = HashMap.class)
public interface ClientTrackingMapper {
    ClientTrackingMapper INSTANCE = Mappers.getMapper(ClientTrackingMapper.class);

    ClientTracking clientTrackingDtoToClientTracking(ClientTrackingDto clientTrackingDto);
    ClientTrackingDto clientTrackingToClientTrackingDto(ClientTracking clientTracking);
}
