package com.complyt.repositories;

import com.complyt.domain.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class AddressQueryBuilder implements QueryBuilder<Address> {

    @Override
    public Query build(@NonNull Address address) {
        Query query = Query.query(Criteria.where("requestAddress.zip").is(address.zip()));

        Optional.ofNullable(address.city()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("address.city").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(address.street()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("address.street").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(address.county()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("address.county").regex(escapedSearchString, "i"));
        });

        return query;
    }
}