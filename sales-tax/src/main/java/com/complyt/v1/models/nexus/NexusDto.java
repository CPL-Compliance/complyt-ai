package com.complyt.v1.models.nexus;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;

@With
public record NexusDto(
        @NotNull(message = "NexusDto.taxableDate " + DtoErrorMessages.NOT_NULL_ERROR) LocalDateTime taxableDate
) {
}

