package com.complyt.v1.models.nexus;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;

import java.time.LocalDateTime;

@With
public record NexusDto(
        @NotNull LocalDateTime taxableDate
) {
}

