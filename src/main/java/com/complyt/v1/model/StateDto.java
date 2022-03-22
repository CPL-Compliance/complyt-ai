package com.complyt.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StateDto {
    private String name;
    private double salesTaxRate;
}