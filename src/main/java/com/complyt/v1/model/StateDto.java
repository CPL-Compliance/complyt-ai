package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Schema(name = "State")
public class StateDto {
    private double salesTaxRate;
    private String abbreviation;
    private String code;
    private String name;
    private List<NexusDto> nexuses;
}