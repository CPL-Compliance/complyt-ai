package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TokenMapperTest {
    @Test
    void tokenToTokenDto_Token_returnTokenDto() {
        // Given
        Token token = TestUtilities.createToken();
        TokenDto tokenDto = new TokenDto(token.getAccessToken(), token.getScope(), token.getExpiresIn(),
                token.getTokenType(), token.getCreatedAt(), token.getExpireAt());

        // When
        TokenDto actualTokenDto = TokenMapper.INSTANCE.tokentoTokenDto(token);

        // Then
        assertEquals(tokenDto, actualTokenDto);
    }

    @Test
    void tokenToTokenDto_TokenIsNull_returnNull() {

        // When
        TokenDto actualTokenDto = TokenMapper.INSTANCE.tokentoTokenDto(null);

        // Then
        assertNull(actualTokenDto);
    }
}