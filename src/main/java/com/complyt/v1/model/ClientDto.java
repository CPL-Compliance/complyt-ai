package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Setter
@ApiModel("Client")
public class ClientDto {
    private String id;
    private String name;
    private AddressDto address;
    private List<ObjectId> ordersId;
}