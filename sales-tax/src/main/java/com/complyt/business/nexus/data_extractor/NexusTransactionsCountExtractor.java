package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationChecker;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NexusTransactionsCountExtractor implements NexusDataExtractor<Long, List<Transaction>> {

    @NonNull
    private ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;

    @Override
    public Long extract(@NonNull List<Transaction> transactions, @NonNull NexusStateRule nexusStateRule) {
        long count = 0;

        for (Transaction transaction : transactions) {
            boolean itemsQualify = itemsNexusStateRuleQualificationChecker.check(new Pair(transaction.getItems(), nexusStateRule));
            boolean transactionIsNotOfTypeRefund = transaction.getTransactionType() != TransactionType.REFUND;
            count += itemsQualify && transactionIsNotOfTypeRefund ? 1 : 0;
        }

        return count;
    }
}
