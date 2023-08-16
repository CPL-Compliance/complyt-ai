package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.v1.models.CredentialsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CredentialsMapperTest {
    Credentials credentials;
    CredentialsDto credentialsDto;

    @BeforeEach
    void setUp() {
        credentials = TestUtilities.createCredentials();
        credentialsDto = TestUtilities.createCredentialsDto();
        credentials = TestUtilities.createCredentials(credentialsDto.clientId(), credentialsDto.clientSecret());
    }

    @Test
    void credentialsDtoTocredentials() {
        // When
        Credentials actualCredentials = CredentialsMapper.INSTANCE.credentialsDtoTocredentials(credentialsDto);

        // Then
        assertEquals(credentials, actualCredentials);
    }
}