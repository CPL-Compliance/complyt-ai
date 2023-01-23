package io.complyt.files.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.With;

@With
@Schema(name = "File")
public record FileDto(@NonNull @Size(max = 2048, message = "Link should be 2048 characters maximum") @NotBlank String link) {
}
