package io.complyt.domain;

import lombok.With;

@With
// Todo should be removed after phase 1
public record TempAddressData(String city, String country, String county, String state,
                              String street, String zip, boolean isPartial, double score) implements AddressData {

}
