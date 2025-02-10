package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

import java.util.Map;

@With
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereScoring (
        double queryScore,
        HereFieldScore fieldScore
) {
}
