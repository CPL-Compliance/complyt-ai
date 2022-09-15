package com.complyt.utils.query;

import com.complyt.domain.Nexus;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class NexusTransactionsSearchQueryBuilder {

    @NonNull
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Query buildNexusTransactionsSearch(@NonNull Nexus nexusInfo, @NonNull NexusStateRule nexusStateRule, @NonNull LocalDateTime referenceDate) {
        Query timeFrameQuery = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule, referenceDate);
        return timeFrameQuery
                .addCriteria(Criteria.where("shippingAddress.state")
                        .is(nexusStateRule.getState().getAbbreviation()));
    }
}
