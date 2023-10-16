package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ApiKeyMapper {
    ApiKeyMapper INSTANCE = Mappers.getMapper(ApiKeyMapper.class);

    default ApiKey apiKeyDtoToApiKey(@NonNull ApiKeyDto apiKeyDto){
        return new ApiKey(apiKeyDto.apiKey());
    }
}
