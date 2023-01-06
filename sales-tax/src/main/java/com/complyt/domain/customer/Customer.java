package com.complyt.domain.customer;

import com.complyt.annotations.Default;
import com.complyt.domain.Address;
import com.complyt.domain.ComplytEntity;
import com.complyt.domain.timestamps.Timestamps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Document(collection = "customer")
@With
public class Customer extends ComplytEntity {
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

    @Default
    public Customer(final UUID complytId, final String id, final String externalId, final String source, final String name, final Address address, final String tenantId, final CustomerType customerType, final Timestamps internalTimestamps, final Timestamps externalTimestamps) {
        super(complytId);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.name = name;
        this.address = address;
        this.tenantId = tenantId;
        this.customerType = customerType;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
    }

    public Customer(final String id, final String externalId, final String source, final String name, final Address address, final String tenantId, final CustomerType customerType, final Timestamps internalTimestamps, final Timestamps externalTimestamps) {
        super(null);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.name = name;
        this.address = address;
        this.tenantId = tenantId;
        this.customerType = customerType;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
    }

    @Override
    public Customer withComplytId(UUID complytId) {
        return this.complytId == complytId ?
                this : new Customer(
                complytId,
                this.id,
                this.externalId,
                this.source,
                this.name,
                this.address,
                this.tenantId,
                this.customerType,
                this.internalTimestamps,
                this.externalTimestamps
        );
    }
}
