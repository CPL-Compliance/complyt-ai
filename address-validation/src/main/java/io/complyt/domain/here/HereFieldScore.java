package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereFieldScore(
        double state,
        double city,
        List<Double> streets
) {
}
