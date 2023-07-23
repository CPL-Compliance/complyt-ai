package testUtils;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface TestUtilities {
    String linkStr = "https://youtu.be/dQw4w9WgXcQ";
    String tenantId = UUID.randomUUID().toString();

    static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .claim("tenant_id", "it_tenant");
    }

    static ApiKey createFile() {
        return new ApiKey(UUID.randomUUID(), ObjectId.get().toString(), tenantId, linkStr);
    }

    static ApiKey createFile(UUID complytId, String id) {
        return new ApiKey(complytId, id, tenantId, linkStr);
    }

    static ApiKeyDto createApiKeyDto() {
        return new ApiKeyDto(UUID.randomUUID(), linkStr);
    }
}
