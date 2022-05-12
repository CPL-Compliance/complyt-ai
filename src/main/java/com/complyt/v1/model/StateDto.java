package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(name = "State")
public class StateDto {
    private double salesTaxRate;
    private String abbreviation;
    private String code;
    private String name;
    private List<NexusDto> nexuses;
}