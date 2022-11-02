package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationCheck;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NexusTransactionCountExtractor implements INexusDataExtractor<Integer, Transaction> {

    @NonNull
    private ItemsNexusStateRuleQualificationCheck itemsNexusStateRuleQualificationCheck;

    @Override
    public Integer extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        final int COUNTED = 1, NOT_COUNTED = 0;
        boolean itemsQualify = itemsNexusStateRuleQualificationCheck.check(new Pair(transaction.getItems(), nexusStateRule));

        return itemsQualify ? COUNTED : NOT_COUNTED;
    }
}
