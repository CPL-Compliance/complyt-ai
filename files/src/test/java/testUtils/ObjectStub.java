package testUtils;

import io.complyt.files.domain.File;
import io.complyt.files.v1.models.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ObjectStub {
    private String linkStr = "http://localhost";
    private String tenantId = UUID.randomUUID().toString();

    public File createFile() {
        return new File(UUID.randomUUID(), ObjectId.get().toString(), tenantId, linkStr );
    }

    public File createFile(UUID complytId, String id) {
        return new File(complytId, id, tenantId, linkStr );
    }

    public FileDto createFileDto() {
        return  new FileDto(UUID.randomUUID(), linkStr);
    }

    public FileDto createFileDto(UUID complytId) {
        return  new FileDto(complytId, linkStr);
    }
}
