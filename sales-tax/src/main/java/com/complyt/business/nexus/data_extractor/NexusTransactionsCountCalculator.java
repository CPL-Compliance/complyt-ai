//package com.complyt.business.nexus.data_extractor;
//
//import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationChecker;
//import com.complyt.domain.nexus.NexusStateRule;
//import com.complyt.domain.transaction.Transaction;
//import com.complyt.domain.transaction.TransactionType;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import org.javatuples.Pair;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//@Component
//@AllArgsConstructor
//public class NexusTransactionsCountCalculator implements NexusDataExtractor<Integer, Transaction> {
//
//    @NonNull
//    private ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;
//
//    @Override
//    public Mono<Integer> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
//        return Mono.fromCallable(() -> {
//            int count = 0;
//            boolean itemsQualify = itemsNexusStateRuleQualificationChecker.check(new Pair(transaction.getItems(), nexusStateRule));
//            boolean transactionIsNotOfTypeRefund = transaction.getTransactionType() != TransactionType.REFUND;
//            count += itemsQualify && transactionIsNotOfTypeRefund ? 1 : 0;
//
//            return count;
//        });
//    }
//
//}
