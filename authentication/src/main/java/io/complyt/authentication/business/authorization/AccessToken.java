package io.complyt.authentication.business.authorization;

public record AccessToken(String accessToken,
                          String scope,
                          int expiresIn,
                          String tokenType){
}
