package com.complyt.domain.customer;

import com.complyt.domain.Address;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Document(collection = "customer")
@With
public class Customer implements ComplytIdProperty {
    private final UUID complytId;
    @Id
    private final String id;
    private final String externalId;
    private final String source;
    private final String name;
    private final Address address;
    private final String tenantId;
    private final CustomerType customerType;
    private final Timestamps internalTimestamps;
    private final Timestamps externalTimestamps;
}
