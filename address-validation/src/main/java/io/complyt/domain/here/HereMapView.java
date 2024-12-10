package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereMapView(
        double west,
        double south,
        double east,
        double north
) {
}
