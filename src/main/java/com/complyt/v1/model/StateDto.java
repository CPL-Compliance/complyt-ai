package com.complyt.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StateDto {

    private String name;

    private double salesTaxRate;
}