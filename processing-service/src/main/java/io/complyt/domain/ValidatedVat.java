package io.complyt.domain;

import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.With;

@With
public record ValidatedVat(
        String countryCode,
        String countryName,
        String vatNumber,
        Boolean valid,
        String name,
        String address,
        Timestamps internalTimestamps
) implements InternalTimestampsProperty {
}
