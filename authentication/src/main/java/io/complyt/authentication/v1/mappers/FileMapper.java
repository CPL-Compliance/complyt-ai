package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    ApiKeyDto fileToFileDto(ApiKey file);
}