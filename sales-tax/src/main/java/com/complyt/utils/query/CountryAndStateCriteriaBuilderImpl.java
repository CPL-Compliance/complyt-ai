package com.complyt.utils.query;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.business.address.UsaAbbreviations;
import com.complyt.domain.transaction.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class CountryAndStateCriteriaBuilderImpl implements CountryAndStateCriteriaBuilder {

    @Override
    public Criteria build(@NonNull Address address) { //todo: I think this is redundant
        return build(address.country(), address.state());
    }

    @Override
    public Criteria build(@NonNull String country, String state) {
        Criteria countryCriteria = countrySearchCriteria(country);

        return CountryIsUsaChecker.isCountryUsa(country) ?
                new Criteria().andOperator(new Criteria()
                        .orOperator(Criteria.where("state.abbreviation").is(state),
                                Criteria.where("state.name").is(state)), countryCriteria) :
                countryCriteria;
    }

    private Criteria countrySearchCriteria(String country) {
        String searchTermCountry = CountryToStandardizedCountry.standardize(country);

        return Criteria.where("country").is(searchTermCountry);
    }
}