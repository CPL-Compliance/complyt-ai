package com.complyt.utils.query;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Component
@AllArgsConstructor
public class NexusTransactionsSearchQueryBuilder {

    @NonNull
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Query buildNexusTransactionsSearch(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule, LocalDate referenceDate, String subsidiary) {
        Query timeFrameQuery = referenceDate == null ? new Query() : timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule, LocalDateTime.of(referenceDate, LocalTime.of(23, 59, 59)));
        timeFrameQuery = CountryIsUsaChecker.isCountryUsa(nexusStateRule.country()) ?
                timeFrameQuery
                        .addCriteria(new Criteria().andOperator(countrySearchCriteria(nexusStateRule.country()), new Criteria().orOperator(
                                Criteria.where("shippingAddress.state").is(nexusStateRule.state().getAbbreviation())
                                , Criteria.where("shippingAddress.state").is(nexusStateRule.state().getName())))) :
                timeFrameQuery.addCriteria(countrySearchCriteria(nexusStateRule.country()));
        timeFrameQuery.addCriteria(Criteria.where("subsidiary").is(subsidiary));

        return timeFrameQuery;
    }

    private Criteria countrySearchCriteria(String country) {
        String searchTermCountry = CountryToStandardizedCountry.standardize(country);

        return Criteria.where("shippingAddress.country").is(searchTermCountry);
    }
}