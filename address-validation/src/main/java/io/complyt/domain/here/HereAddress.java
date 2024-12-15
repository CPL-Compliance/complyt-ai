package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
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
