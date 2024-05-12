package com.complyt.utils.query;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.address.CountryToStandardizedCountry;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class NexusTransactionsSearchQueryBuilder {

    @NonNull
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Query buildNexusTransactionsSearch(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule, @NonNull LocalDateTime referenceDate, String subsidiary) {
        Query timeFrameQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule, referenceDate);
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