package io.complyt.files.v1.mappers;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ComplytFileMapper {
    ComplytFileMapper INSTANCE = Mappers.getMapper(ComplytFileMapper.class);

    ComplytFileDto complytFileToComplytFileDto(ComplytFile complytFile);

    ComplytFile complytFileDtoToComplytFile(ComplytFileDto complytFileDto);

    ComplytFileMetadataDto complytFileMetadataToComplytFileMetadataDto(ComplytFileMetadata complytFileMetadata);
}
