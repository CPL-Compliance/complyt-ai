package io.complyt.domain.customer;

import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import io.complyt.domain.transaction.Address;
import lombok.With;

import java.util.UUID;

@With
public record Customer(
        UUID complytId,
        String id,
        String externalId,
        String source,
        String name,
        Address address,
        String tenantId,
        String email,
        CustomerType customerType,
        Timestamps internalTimestamps,
        Timestamps externalTimestamps,
        String comment,
        CustomerStatus customerStatus
) implements ComplytIdProperty, InternalTimestampsProperty {
}
