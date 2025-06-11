package io.complyt.domain.transaction;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Builder
@With
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Accessors(chain = true)
public class GeoRecord {

    String id;
    String zip;
    String state;
}