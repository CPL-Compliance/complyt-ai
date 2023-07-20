package testUtils;

import io.complyt.files.domain.ApiKey;
import io.complyt.files.v1.models.FileDto;
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

    static FileDto createFileDto() {
        return new FileDto(UUID.randomUUID(), linkStr);
    }

    static FileDto createFileDto(UUID complytId) {
        return new FileDto(complytId, linkStr);
    }
}
