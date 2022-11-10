package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationChecker;
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
public class NexusTransactionCountExtractor implements NexusDataExtractor<Integer, Transaction> {

    @NonNull
    private ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;

    @Override
    public Integer extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        final int COUNTED = 1, NOT_COUNTED = 0;
        boolean itemsQualify = itemsNexusStateRuleQualificationChecker.check(new Pair(transaction.getItems(), nexusStateRule));

        return itemsQualify ? COUNTED : NOT_COUNTED;
    }
}
