package io.complyt.filing.v1.mappers;

import io.complyt.filing.domain.Link;
import io.complyt.filing.v1.model.LinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface LinkMapper {
    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    LinkDto linkToLinkDto(Link link);
}