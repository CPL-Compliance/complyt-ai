package com.complyt.repositories.internal_rates.address_standardization;

import java.util.List;
import java.util.regex.Pattern;

public interface AddressStandardizationRules {
    /**
     * Pre-compiled regex patterns for efficient address standardization.
     * Compiling patterns once avoids repeated runtime compilation,
     * reducing resource usage and improving performance.
     */

    Pattern NON_LETTER_PATTERN = Pattern.compile("[^a-zA-Z\\s-]"); // Includes spaces, hyphens

    List<Pattern> STANDARDIZATION_PATTERNS = List.of(
            Pattern.compile("\\bsaint\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bst\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bmount\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bmt\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bport\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bstation\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\btwp\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\s+\\(city\\)", Pattern.CASE_INSENSITIVE),

            // Directional prefixes - will be removed
            Pattern.compile("\\bnorth\\s+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bsouth\\s+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\beast\\s+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bwest\\s+", Pattern.CASE_INSENSITIVE)
    );
}
