package io.complyt.domain;

import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@With
@Accessors(chain = true)
public class ValidatedVat implements InternalTimestampsProperty {
    String countryCode;
    String countryName;
    String vatNumber;
    Boolean valid;
    String name;
    String address;
    Timestamps internalTimestamps;
}
