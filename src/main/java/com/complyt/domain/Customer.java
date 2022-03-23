package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Customer {

    @Id
    private String id;
    private String name;
    private Address address;
}