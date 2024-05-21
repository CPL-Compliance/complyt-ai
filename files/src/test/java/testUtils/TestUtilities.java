package testUtils;

import org.bson.Document;
import io.complyt.files.domain.File;
import io.complyt.files.v1.models.FileDto;
import org.bson.types.Binary;
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

    static File createFile() {
        return new File(UUID.randomUUID(), ObjectId.get().toString(), tenantId, linkStr);
    }

    static File createFile(UUID complytId, String id) {
        return new File(complytId, id, tenantId, linkStr);
    }

    static FileDto createFileDto() {
        return new FileDto(UUID.randomUUID(), linkStr);
    }

    static FileDto createFileDto(UUID complytId) {
        return new FileDto(complytId, linkStr);
    }

    static Document fileDocument() {
        return new Document()
                .append("complytId",UUID.randomUUID().toString().getBytes()) // UUID as binary data
                .append("_id",  new ObjectId("65b6a7f8f930555db9c7c246"))  // Typically this would be set automatically by MongoDB if using ObjectIds
                .append("tenantId", "tenantIdExample")
                .append("link", "http://example.com/token");
    }
}
