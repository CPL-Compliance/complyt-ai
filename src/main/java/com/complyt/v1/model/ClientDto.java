package com.complyt.v1.model;

import com.complyt.domain.Address;
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
    private List<ObjectId> ordersId;
}