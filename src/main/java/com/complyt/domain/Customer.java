package com.complyt.domain;

import lombok.*;
import org.bson.types.ObjectId;
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
    private final String id;
    private final String externalId;
    private final String name;
    private final Address address;
    private final ObjectId clientId;
}