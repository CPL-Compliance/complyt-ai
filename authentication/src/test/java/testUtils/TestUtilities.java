package testUtils;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface TestUtilities {
    String apiKey = "929f1749-cfa7-46c0-8b6f-ee9602c7819c";
    String tenantId = UUID.randomUUID().toString();

    static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .claim("tenant_id", "it_tenant");
    }

    static Token createToken() {
        return new Token("", "", "", "", apiKey);
    }

    static Token createToken(UUID complytId, String id) {
        return new Token("", "", "", "", apiKey);
    }

    static TokenDto createTokenDto() {
        return new TokenDto(apiKey);
    }
}
