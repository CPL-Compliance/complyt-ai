package io.complyt.authentication.domain.mappers;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.Auth0AccessToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class Auth0AccessTokenToAccessTokenTest {
    AccessToken accessToken;
    Auth0AccessToken auth0AccessToken;

    @BeforeEach
    void setUp() {
        accessToken = TestUtilities.createAccessToken();
        auth0AccessToken = TestUtilities.createAuth0AccessToken();
    }

    @Test
    void map_auth0AccessTokenToAccessToken() {
        // When
        AccessToken actualAccessToken = Auth0AccessTokenToAccessToken.INSTANCE.map(auth0AccessToken);

        // Then
        assertEquals(accessToken, actualAccessToken);
    }

    @Test
    void map_auth0AccessTokenIsNull_returnNull() {
        // When
        AccessToken actualAccessToken = Auth0AccessTokenToAccessToken.INSTANCE.map(null);

        // Then
        assertNull(actualAccessToken);
    }
}