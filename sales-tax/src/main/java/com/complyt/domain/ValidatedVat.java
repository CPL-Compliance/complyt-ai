package com.complyt.domain;

import com.complyt.domain.properties.InternalTimestampsProperty;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@With
@Accessors(chain = true)
@Document(collection = "validated_vat")
public class ValidatedVat implements InternalTimestampsProperty {
    String countryCode;
    String countryName;
    String vatNumber;
    Boolean valid;
    String name;
    String address;
    Timestamps internalTimestamps;
}
