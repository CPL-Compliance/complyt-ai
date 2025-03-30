package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.v1.models.PartnershipDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface PartnershipMapper {
    PartnershipMapper INSTANCE = Mappers.getMapper(PartnershipMapper.class);

    PartnershipDto partnershipToPartnershipDto(Partnership partnership);
}