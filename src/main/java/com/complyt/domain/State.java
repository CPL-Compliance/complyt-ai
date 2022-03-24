package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "state")
public class State {
    @Id
    private String id;
    private double salesTaxRate;
    private String abbreviation;
    private String code;
    private String name;
    private List<Nexus> nexuses;
}