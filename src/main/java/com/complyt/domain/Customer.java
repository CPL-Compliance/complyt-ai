package com.complyt.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class Customer {

    @Id
    private String id;
    private String externalId;
    private String name;
    private Address address;
}