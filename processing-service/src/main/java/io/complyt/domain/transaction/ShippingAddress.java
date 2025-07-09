package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShippingAddress(String city, String country, String county, String state, String street, String region, String zip,
                              boolean isPartial, MatchedAddressData matchedAddressData) implements BaseAddress {
}
