package testUtils;

import io.complyt.files.domain.File;
import io.complyt.files.v1.models.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.UUID;

public interface TestUtilities {
     String linkStr = "https://youtu.be/dQw4w9WgXcQ";
     String tenantId = UUID.randomUUID().toString();

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
}
