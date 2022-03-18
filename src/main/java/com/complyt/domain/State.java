package com.complyt.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "state")
public class State {
    @Id
    private String id;

    private float salesTaxRate;

    private String abbreviation;

    private String code;

    private String name;

    private List<Nexus> nexuses;

    public State(float salesTaxRate, String abbreviation, String code, String name, List<Nexus> nexuses) {
        this.salesTaxRate = salesTaxRate;
        this.abbreviation = abbreviation;
        this.code = code;
        this.name = name;
        this.nexuses = nexuses;
    }
}