package io.complyt.files.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.With;
import org.springframework.http.codec.multipart.FilePart;

@With
@Schema(name = "ComplytFileFile")
public record ComplytFileDto(
        @NotNull FilePart file,
        ComplytFileMetadataDto metadata
) {
}
