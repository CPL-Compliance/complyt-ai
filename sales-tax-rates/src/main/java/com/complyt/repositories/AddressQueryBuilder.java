package com.complyt.repositories;

import com.complyt.domain.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddressQueryBuilder implements QueryBuilder<Address> {

    @Override
    public Query build(@NonNull Address address) {
        Query query = Query.query(Criteria.where("address.zip").is(address.zip()));
        Optional.ofNullable(address.city())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.city").regex(value, "i")));
        Optional.ofNullable(address.street())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.street").regex(value, "i")));
        Optional.ofNullable(address.county())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.county").regex(value, "i")));

        return query;
    }

}