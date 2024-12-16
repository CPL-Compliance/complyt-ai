package com.complyt.repositories.internal_rates.criteria;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ZipCriteriaBuilder {

    private static final Pattern ZIP_PATTERN = Pattern.compile("^(\\d{5})(?:-(.*))?$");
    private static final String SUB_ZIP_PATTERN = "\\d{4}";


    public List<Criteria> build(String zip) {
        // Validate and parse ZIP codes with optional plus-four parts.
        // XXXXX -> e.g., 12345
        // XXXXX-XXXX -> e.g., 12345-6789
        Matcher matcher = ZIP_PATTERN.matcher(zip);

        List<Criteria> criteriaList = new ArrayList<>();

        if (matcher.matches()) {
            // Base ZIP code (e.g., "12345")
            String baseZip = matcher.group(1);
            criteriaList.add(Criteria.where("address.zip").is(baseZip));

            // Plus-four code (e.g., "6789"), default to 0 if not present or invalid
            String plusFourPart = matcher.group(2);
            int plusFour = 0;

            if (plusFourPart != null) {
                if (plusFourPart.matches(SUB_ZIP_PATTERN)) {
                    plusFour = Integer.parseInt(plusFourPart);
                } else {
                    log.warn("Non-numeric plus-four detected: '{}', defaulting to 0", plusFourPart);
                }
            }

            // Add criteria for plus-four ranges
            criteriaList.add(new Criteria().andOperator(
                    Criteria.where("address.lowerPlusFourDigits").lte(plusFour),
                    Criteria.where("address.upperPlusFourDigits").gte(plusFour)
            ));
        } else {
            log.warn("Warning: Unexpected ZIP code format: '{}'", zip);
        }
        return criteriaList;
    }
}
