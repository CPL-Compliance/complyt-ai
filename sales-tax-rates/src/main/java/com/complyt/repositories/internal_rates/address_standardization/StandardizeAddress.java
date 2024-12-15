package com.complyt.repositories.internal_rates.address_standardization;

import com.complyt.domain.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.complyt.repositories.internal_rates.address_standardization.AddressStandardizationRules.NON_LETTER_PATTERN;
import static com.complyt.repositories.internal_rates.address_standardization.AddressStandardizationRules.STANDARDIZATION_PATTERNS;

@Slf4j
@Component
public class StandardizeAddress implements AddressStandardize {
    private static final String EMPTY_STRING = "";

    @Override
    public Address standardize(Address address) {
        String standardizedCounty = standardizeField(address.county());
        Address standardizedAddress = address.withCounty(standardizedCounty); // Clone the address;

        log.info("Standardizing county. Before: {}, After: {}", address.county(), standardizedAddress.county());
        return standardizedAddress;
    }

    private String standardizeField(String field) {
        return (field == null) ? null : applyStandardization(field);
    }

    private String applyStandardization(String field) {
        String result = field.toLowerCase();
        result = NON_LETTER_PATTERN.matcher(result).replaceAll(EMPTY_STRING);

        // Apply all patterns
        for (Pattern pattern : STANDARDIZATION_PATTERNS) {
            result = pattern.matcher(result).replaceAll(EMPTY_STRING);
        }
        return result.trim();
    }
}
