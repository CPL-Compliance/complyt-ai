package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Schema(name = "State")
public class StateDto {
    private String abbreviation;
    private String code;
    private String name;
}