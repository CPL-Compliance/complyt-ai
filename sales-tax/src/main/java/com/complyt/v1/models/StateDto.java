package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String abbreviation;
    private String code;
    private String name;
}