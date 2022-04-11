package com.complyt.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Setter
@NoArgsConstructor
public class Order {
    @Id
    private String id;

    private ObjectId customerId;
    private String type;
    private int units;
    private int price;
}
