package com.complyt.utils.query;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.business.address.UsaAbbreviations;
import com.complyt.domain.transaction.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CountryAndStateCriteriaBuilderImpl implements CountryAndStateCriteriaBuilder {

    @Override
    public Criteria build(@NonNull Address address) { //todo: I think this is redundant
        return build(address.country(), address.state());
    }

    @Override
    public Criteria build(@NonNull String country, String state) {
        Criteria criteria = CountryIsUsaChecker.isCountryUsa(country) ?
                new Criteria().andOperator(new Criteria().orOperator(Criteria.where("state.abbreviation").is(state), Criteria.where("state.name").is(state)),
                                new Criteria().orOperator(listOfUsaAbbreviationsOptionsCriteria())) :
                new Criteria().orOperator(listOfNonUsaAbbreviationCriteria(country));

        return criteria;
    }

    private List<Criteria> listOfUsaAbbreviationsOptionsCriteria() { //todo: this is a duplicated code from nexustransactionsearchquerybuilder
        return UsaAbbreviations.usaAbbreviationsList.stream()
                .map(abbreviation -> Criteria.where("country").is(abbreviation.toUpperCase())).collect(Collectors.toList());
    }

    private List<Criteria> listOfNonUsaAbbreviationCriteria(String country) {//todo: duplicated code almost
        return SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase()).stream()
                .map(name -> Criteria.where("country").is(name.toUpperCase())).collect(Collectors.toList());
    }

}