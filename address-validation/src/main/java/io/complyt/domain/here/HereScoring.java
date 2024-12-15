package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

@With
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereScoring (
        double queryScore,
        HereFieldScore fieldScore,
        double houseNumber,
        double postalCode
) {
}
