package com.complyt.v1.models.timestamps;

import com.complyt.utils.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@With
@Schema(name = "Timestamps")
public record TimestampsDto(@Valid @NotBlank(message = "Created date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "Created date is in illegal format") @NotNull(message = "Created date may not be null") String createdDate,
                            @Valid @NotBlank(message = "Created date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "Updated date is in illegal format") @NotNull(message = "Updated date may not be null") String updatedDate) {

//    public LocalDateTime getCreatedDate() {
//        return LocalDateTime.parse(this.createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//    }
//
//    public LocalDateTime getUpdatedDate() {
//        return LocalDateTime.parse(this.updatedDate);
//    }
}
