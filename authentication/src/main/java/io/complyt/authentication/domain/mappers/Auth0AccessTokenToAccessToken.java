package io.complyt.authentication.domain.mappers;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.Auth0AccessToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface Auth0AccessTokenToAccessToken {
    Auth0AccessTokenToAccessToken INSTANCE = Mappers.getMapper(Auth0AccessTokenToAccessToken.class);

    @Mapping(target = "accessToken", source = "access_token")
    @Mapping(target = "scope", source = "scope")
    @Mapping(target = "expiresIn", source = "expires_in")
    @Mapping(target = "tokenType", source = "token_type")
    AccessToken map(Auth0AccessToken auth0AccessToken);
}
