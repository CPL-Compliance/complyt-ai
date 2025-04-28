package io.complyt.domain;

import io.complyt.business.address.CountryIsUsaChecker;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class AddressQueryBuilder {

    public Query build(@NonNull Address address) {
        Query query = new Query();

        // Conditionally apply the country filter (Only for non-US addresses)
        Optional.ofNullable(address.country()).ifPresent(country -> {
            if (!CountryIsUsaChecker.isCountryUsa(country)) { // Skip for US
                query.addCriteria(Criteria.where("requestAddress.country").is(country));
            }
        });

        Optional.ofNullable(address.zip()).ifPresent(zip ->
                query.addCriteria(Criteria.where("requestAddress.zip").is(zip))
        );

        Optional.ofNullable(address.city()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("requestAddress.city").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(address.street()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("requestAddress.street").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(address.county()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("requestAddress.county").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(address.region()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("requestAddress.region").regex(escapedSearchString, "i"));
        });

        return query;
    }
}