package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "client")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Client {
    @Id
    private String id;
    private String name;
    private Address address;

    @DBRef
    private List<Order> orders;
}
