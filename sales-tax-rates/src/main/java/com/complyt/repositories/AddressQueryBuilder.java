package com.complyt.repositories;

import com.complyt.domain.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

@Component
public class AddressQueryBuilder implements QueryBuilder<Address> {

    HashMap<Boolean, Function<Address, Query>> queries = new HashMap<>() {
        {
            put(Boolean.TRUE, AddressQueryBuilder::buildPartialAddressQuery);
            put(Boolean.FALSE, AddressQueryBuilder::buildFullAddressQuery);
        }
    };

    @Override
    public Query build(@NonNull Address address) {
        Function<Address, Query> queryBuilderFunction = queries.get(address.isPartial());
        Query query = queryBuilderFunction.apply(address);

        return query;
    }

    public static Query buildPartialAddressQuery(Address address) {
        Query query = Query.query(Criteria.where("address.zip").is(address.zip()));
        Optional.ofNullable(address.city())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.city").is(value)));
        Optional.ofNullable(address.street())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.street").is(value)));
        Optional.ofNullable(address.county())
                .ifPresent(value -> query.addCriteria(Criteria.where("address.county").is(value)));

        return query;
    }

    public static Query buildFullAddressQuery(Address address) {
        return Query.query(Criteria
                .where("address.city").is(address.city())
                .and("address.street").is(address.street())
                .and("address.zip").is(address.zip()));
    }

}