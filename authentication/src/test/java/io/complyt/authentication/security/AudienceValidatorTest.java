package io.complyt.authentication.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AudienceValidatorTest {

    AudienceValidator audienceValidator;

    @BeforeEach
    void setup() {
        audienceValidator = new AudienceValidator("Good");
    }

    @Test
    void validate_audienceNotEqual_failure() {
        // Given + When
        Jwt jwt = Jwt.withTokenValue(UUID.randomUUID().toString())
                .header("Alg", "aaa")
                .claim(JwtClaimNames.AUD, "Bad")
                .build();

        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);

        // Then
        long count = oAuth2TokenValidatorResult
                .getErrors()
                .stream()
                .filter(error ->
                        error.getErrorCode().equals("invalid_token") &&
                                error.getDescription().equals("The required audience is missing")).count();

        assertEquals(1, count);
    }

    @Test
    void validate_audienceEqual_success() {
        // Given + When
        Jwt jwt = Jwt.withTokenValue(UUID.randomUUID().toString())
                .header("Alg", "aaa")
                .claim(JwtClaimNames.AUD, "Good")
                .build();

        OAuth2TokenValidatorResult oAuth2TokenValidatorResult = audienceValidator.validate(jwt);

        // Then
        assertTrue(oAuth2TokenValidatorResult.getErrors().isEmpty());
    }
}