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
    private Token inputToken;
    private TokenDto inputTokenDto;
    private Token outputToken;
    private TokenDto outputTokenDto;

    @BeforeEach
    void setUp() {
        inputTokenDto = TestUtilities.createTokenDto();
        outputTokenDto = TestUtilities.createOutputTokenDto();
        outputToken = TestUtilities.createOutputToken();
        inputToken = TestUtilities.createInputToken();
    }

//    @Test
//    void tokenToTokenDto_Token_returnTokenDto() {
//        // Given
//        TokenDto expectedTokenDto = outputTokenDto;
//
//        // When
//        TokenDto actualTokenDto = TokenMapper.INSTANCE.tokentoTokenDto(outputToken);
//
//        // Then
//        assertEquals(expectedTokenDto, actualTokenDto);
//    }
//
//    @Test
//    void tokenDtoToToken_tokenDto_returnToken() {
//        // Given
//        Token expectedToken = inputToken;
//
//        // When
//        Token actualToken = TokenMapper.INSTANCE.tokenDtoToToken(inputTokenDto);
//
//        // Then
//        assertEquals(expectedToken, actualToken);
//    }
}