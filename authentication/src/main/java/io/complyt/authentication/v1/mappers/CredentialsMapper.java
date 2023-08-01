package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.v1.models.CredentialsDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CredentialsMapper {
    CredentialsMapper INSTANCE = Mappers.getMapper(CredentialsMapper.class);

    Credentials credentialsDtoTocredentials(CredentialsDto credentialsDto);

    CredentialsDto credentialstoCredentialsDto(Credentials credentials);
}
