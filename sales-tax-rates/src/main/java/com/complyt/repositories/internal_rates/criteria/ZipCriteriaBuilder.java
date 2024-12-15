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

    private static final Pattern ZIP_PATTERN = Pattern.compile("^(\\d{5})(?:-(\\d{4}))?$");

    public List<Criteria> build(String zip) {
        // Define a regex pattern to match ZIP codes with optional plus-four part
        // zip may look like the followings:
        // XXXXX example 12345
        // XXXXX-XXXX example 12345-6789
        Matcher matcher = ZIP_PATTERN.matcher(zip);

        // Initialize List of Criteria object
        List<Criteria> criteriaList = new ArrayList<>();

        // Check if the zip matches the pattern
        if (matcher.matches()) {
            // Match the base ZIP code
            String baseZip = matcher.group(1);
            criteriaList.add(Criteria.where("address.zip").is(baseZip));

            // Handle plus-four if available, otherwise default to 0
            int plusFour = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

            // Optimized criteria to focus on plus-four ranges, assuming most have `hasPlusFourZipCode` as true
            criteriaList.add(new Criteria().andOperator(
                    Criteria.where("address.lowerPlusFourDigits").lte(plusFour),
                    Criteria.where("address.upperPlusFourDigits").gte(plusFour)
            ));

            // arriving here means the zip received does not match the 12345(-6789) format
            // we split the zip by - and getting the first part
        } else {
            //todo check with NIV if it's needed
            log.info("Warning: ZIP code format is unexpected, using first part only.");
        }

        return criteriaList;
    }
}
