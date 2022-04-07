package com.complyt.v1.model;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class OrderDto {
    private String id;
    private ObjectId customer_id;
    private String type;
    private int units;
    private int price;
}
