package io.complyt.files.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@With
@Schema(name = "File")
public record FileDto(@NonNull @Size(max = 2048, message = "Link should be 2048 characters maximum") @NotBlank String link) {
}
