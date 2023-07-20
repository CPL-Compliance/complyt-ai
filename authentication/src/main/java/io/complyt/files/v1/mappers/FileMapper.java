package io.complyt.files.v1.mappers;

import io.complyt.files.domain.ApiKey;
import io.complyt.files.v1.models.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    FileDto fileToFileDto(ApiKey file);
}