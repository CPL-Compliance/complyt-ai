package com.complyt.v1.models.nexus;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import com.complyt.v1.config.regex.LocalDateRegex;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@With
public record DateWrapperDto(
        @Pattern(regexp = ISO8601Regex.expression, message = "date " + DtoErrorMessages.LOCALDATE_FORMAT_ERROR) String date
) {
}

