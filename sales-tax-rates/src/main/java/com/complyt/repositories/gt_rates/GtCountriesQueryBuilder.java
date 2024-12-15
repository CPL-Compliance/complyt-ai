package com.complyt.repositories.gt_rates;

import com.complyt.domain.gt.GtAddress;
import com.complyt.repositories.QueryBuilder;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

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
            // if region does not exist - get the basic country rate, and not a rate with incorrect region
            query.addCriteria(Criteria.where("gtAddress.region").exists(false));
        }

        return query;
    }
}