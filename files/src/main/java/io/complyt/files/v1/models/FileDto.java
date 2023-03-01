package io.complyt.files.v1.models;

import io.complyt.files.v1.models.properties.ComplytIdPropertyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "File")
public record FileDto(UUID complytId,
                      @NonNull @Size(max = 2048, message = "Link should be 2048 characters maximum") @NotBlank(message = "Link shouldn't be blank") String link)
        implements ComplytIdPropertyDto {
}
