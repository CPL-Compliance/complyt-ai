package io.complyt.domain;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@With
@Accessors(chain = true)
public class VatDetailsToValidate {
    String countryCode;
    String vatNumber;
}
