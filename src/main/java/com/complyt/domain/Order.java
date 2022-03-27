package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class Order {
    @Id
    private String id;

    @DBRef
    private Customer customer;
    private String type;
    private int units;
    private int price;
}
