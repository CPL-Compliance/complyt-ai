package com.complyt.repositories;

import com.complyt.domain.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
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
        return Query.query(Criteria.where("address.zip").is(address.zip()));
    }

    public static Query buildFullAddressQuery(Address address) {
        return Query.query(Criteria
                .where("address.city").is(address.city())
                .and("address.street").is(address.street())
                .and("address.zip").is(address.zip()));
    }

}