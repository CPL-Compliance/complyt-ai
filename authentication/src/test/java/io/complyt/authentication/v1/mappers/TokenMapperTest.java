package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TokenMapperTest {
    private Token token;
    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        tokenDto = TestUtilities.createTokenDto();
        token = TestUtilities.createToken(tokenDto.apiKey());
    }

    @Test
    void tokenToTokenDto_Token_returnTokenDto() {
        // Given
        TokenDto givenTokenDto = tokenDto;

        // When
        TokenDto actualTokenDto = TokenMapper.INSTANCE.tokentoTokenDto(token);

        // Then
        assertEquals(givenTokenDto, actualTokenDto);
    }

    @Test
    void tokenDtoToToken_TokenDto_returnToken() {
        // Given
        Token givenToken = token;

        // When
        Token actualToken = TokenMapper.INSTANCE.tokenDtoToToken(tokenDto);

        // Then
        assertEquals(givenToken, actualToken);
    }
}