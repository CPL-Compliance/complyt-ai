package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MandatoryAddress(String city, String country, String county, String state, String street, String region,
                               String zip, Boolean isPartial) {
}