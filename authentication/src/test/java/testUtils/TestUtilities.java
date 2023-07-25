package testUtils;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;


public class TestUtilities {
    static String apiKey = "929f1749-cfa7-46c0-8b6f-ee9602c7819c";

    public static Token createToken() {
        return new Token(apiKey, "", "", "", "");
    }

    public static Token createToken(String apiKey) {
        return new Token(apiKey, "", "", "", "");
    }

    public static TokenDto createTokenDto() {
        return new TokenDto(apiKey, "" , "", "", "");
    }
}
