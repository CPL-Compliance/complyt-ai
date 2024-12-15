package com.complyt.repositories.internal_rates.criteria;

import com.complyt.domain.Address;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@AllArgsConstructor
@Slf4j
public class CountyZipCriteriaBuilder implements CriteriaBuilder {

    @NonNull
    private final ZipCriteriaBuilder zipCriteriaBuilder;

    @Override
    public Criteria build(Address address) {
        Criteria criteria = new Criteria();

        if (address.zip() != null) {
            criteria.andOperator(zipCriteriaBuilder.build(address.zip()));
        }

        if (address.county() != null) {
            criteria.and("address.county").regex(Pattern.quote(address.county().toUpperCase()), "i");
        }
        return criteria;
    }
}
