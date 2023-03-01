package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Schema(name = "State")
public record StateDto(
        @NotBlank(message = "Abbreviation may not be blank") @Size(min = 1, max = 256, message = "Abbreviation should be 1-256 characters maximum") String abbreviation,
        @NotBlank(message = "Code may not be blank") @Size(min = 1, max = 256, message = "Code should be 1-256 characters maximum") String code,
        @NotBlank(message = "Name may not be blank") @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum") String name) {
}