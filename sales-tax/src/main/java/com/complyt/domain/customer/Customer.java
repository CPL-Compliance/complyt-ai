package com.complyt.domain.customer;

import com.complyt.domain.Address;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "customer")
public class Customer {
    @Id
    private final String id;
    private final String externalId;
    private final String name;
    private final Address address;
    private final String tenantId;
    private final CustomerType customerType;
}
