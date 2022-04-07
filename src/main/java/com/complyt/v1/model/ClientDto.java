package com.complyt.v1.model;

import com.complyt.domain.Address;
import com.complyt.domain.Order;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Setter
public class ClientDto {
    private String id;
    private String name;
    private Address address;
    private List<ObjectId> orders_id;
}