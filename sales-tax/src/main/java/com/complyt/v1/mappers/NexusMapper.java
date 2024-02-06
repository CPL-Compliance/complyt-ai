package com.complyt.v1.mappers;

import com.complyt.domain.Nexus;
import com.complyt.v1.models.nexus.NexusDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, imports = HashMap.class)
public interface NexusMapper {
    NexusMapper INSTANCE = Mappers.getMapper(NexusMapper.class);

    Nexus nexusDtoToNexus(NexusDto nexusDto);
    NexusDto nexusToNexusDto(Nexus nexus);
}
