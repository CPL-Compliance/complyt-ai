package io.complyt.files.v1.models;

import io.complyt.files.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@With
@Schema(name = "ComplytFileMetadata")
public record ComplytFileMetadataDto(UUID complytId, Map<String, String> metadata,
                                     String tenantId, OffsetDateTime updateTime,
                                     OffsetDateTime createTime,
                                     @NotBlank(message = "ComplytFileMetadata.Link " + DtoErrorMessages.NOT_NULL_BLANK) String link) {
}