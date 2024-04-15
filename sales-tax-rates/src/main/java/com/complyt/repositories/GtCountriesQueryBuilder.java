package com.complyt.repositories;

import com.complyt.domain.gt.GtAddress;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class GtCountriesQueryBuilder implements QueryBuilder<GtAddress> {
    @Override
    public Query build(@NonNull GtAddress gtAddress) {
        Query query = Query.query(Criteria.where("gtAddress.country").regex(gtAddress.country(), "i"));

        if (gtAddress.region() != null) {
            String escapedSearchString = Pattern.quote(gtAddress.region());
            query.addCriteria(Criteria.where("gtAddress.region").regex(escapedSearchString, "i"));
        } else {
            query.addCriteria(Criteria.where("gtAddress.region").exists(false));
        }

        return query;
    }
}