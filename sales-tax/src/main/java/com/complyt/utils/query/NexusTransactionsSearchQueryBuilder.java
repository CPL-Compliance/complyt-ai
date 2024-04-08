package com.complyt.utils.query;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.business.address.UsaAbbreviations;
import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class NexusTransactionsSearchQueryBuilder {

    @NonNull
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Query buildNexusTransactionsSearch(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule, @NonNull LocalDateTime referenceDate) {
        Query timeFrameQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule, referenceDate);
        timeFrameQuery = CountryIsUsaChecker.isCountryUsa(nexusStateRule.country()) ?
                timeFrameQuery
                        .addCriteria(new Criteria().andOperator(new Criteria().orOperator(listOfUsaAbbreviationsOptionsCriteria()), new Criteria().orOperator(
                                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getAbbreviation())
                                , Criteria.where("shippingAddress.state").is(nexusStateRule.state().getName())))) :
                timeFrameQuery.addCriteria(new Criteria().orOperator(listOfNonUsaAbbreviationCriteria(nexusStateRule.country())));

        return timeFrameQuery;
    }

    private List<Criteria> listOfUsaAbbreviationsOptionsCriteria() {
        return UsaAbbreviations.usaAbbreviationsList.stream()
                .map(abbreviation -> Criteria.where("shippingAddress.country").is(abbreviation.toUpperCase())).collect(Collectors.toList());
    }

    private List<Criteria> listOfNonUsaAbbreviationCriteria(String country) {
        return SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase()).stream()
                .map(name -> Criteria.where("shippingAddress.country").is(name.toUpperCase())).collect(Collectors.toList());
    }
}