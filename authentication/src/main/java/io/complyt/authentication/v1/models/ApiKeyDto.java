package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.models.properties.ComplytIdPropertyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "File")
public record ApiKeyDto(UUID complytId,
                        @Size(min = 1, max = 2048, message = "Link should be 2048 characters maximum") @NotNull(message = "Link shouldn't be blank") String link)
        implements ComplytIdPropertyDto {
}