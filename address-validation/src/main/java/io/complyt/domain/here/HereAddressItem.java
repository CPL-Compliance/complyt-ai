package io.complyt.domain.here;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

@With
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record HereAddressItem(
        String title,
        String id,
        String resultType,
        String localityType,
        HereAddress address,
        HerePosition position,
        HereMapView mapView,
        HereScoring scoring
){

}
