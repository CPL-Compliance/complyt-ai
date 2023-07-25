package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TokenMapper {
    TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

    Token tokenDtoToToken(TokenDto tokenDto);

    TokenDto tokentoTokenDto(Token token);
}