package io.complyt.domain;

import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@With
public record CachedAddressData(String city, String country, String county, String state,
                                String street, String zip, boolean isPartial, double score) implements AddressData {

    public static final CachedAddressData DEFAULT = new CachedAddressData(
            "UNKNOWN", // Default city
            "UNKNOWN", // Default country
            "UNKNOWN", // Default county
            "UNKNOWN", // Default state
            "UNKNOWN", // Default street
            "UNKNOWN", // Default zip
            false,     // Default isPartial
            0.0        // Default score
    );
}