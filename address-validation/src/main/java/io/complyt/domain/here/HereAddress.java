package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
@With
public record HereAddress(
        String label,
        String countryCode ,
        String countryName,
        String stateCode,
        String state,
        String county,
        String city,
        String street,
        String postalCode
) {
}
