package com.complyt.v1.model;

import com.complyt.domain.Nexus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StateDto {
    private double salesTaxRate;
    private String abbreviation;
    private String code;
    private String name;
    private List<Nexus> nexuses;
}