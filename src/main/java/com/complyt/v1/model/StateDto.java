package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ApiModel("State")
public class StateDto {
    private double salesTaxRate;
    private String abbreviation;
    private String code;
    private String name;
    private List<NexusDto> nexuses;
}