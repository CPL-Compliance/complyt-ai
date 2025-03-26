package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

import java.util.List;

@With
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereFieldScore(
        double country,
        double state,
        double city,
        List<Double> streets,
        double postalCode
) {
}
