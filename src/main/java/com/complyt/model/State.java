package com.complyt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", salesTaxRate=" + salesTaxRate +
                ", abbreviation='" + abbreviation + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", nexuses=" + nexuses +
                '}';
    }

    public String getId() {
        return id;
    }

    public float getSalesTaxRate() {
        return salesTaxRate;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<Nexus> getNexuses() {
        return nexuses;
    }
}