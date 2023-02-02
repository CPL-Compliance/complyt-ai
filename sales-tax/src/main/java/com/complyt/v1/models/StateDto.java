package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Schema(name = "State")
public class StateDto {
    @NotBlank(message = "abbreviation may not be blank")
    @Size(min = 1, max = 256, message = "abbreviation should be 1-256 characters maximum")
    private String abbreviation;

    @NotBlank(message = "code may not be blank")
    @Size(min = 1, max = 256, message = "code should be 1-256 characters maximum")
    private String code;

    @NotBlank(message = "name may not be blank")
    @Size(min = 1, max = 256, message = "name should be 1-256 characters maximum")
    private String name;
}