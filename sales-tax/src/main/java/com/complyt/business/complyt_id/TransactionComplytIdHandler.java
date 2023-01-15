package com.complyt.business.complyt_id;

import com.complyt.domain.Transaction;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class TransactionComplytIdHandler implements ComplytIdHandler<Transaction> {
    @Override
    public Mono<Transaction> isComplytIdOfUpdatedEqualsToOld(Transaction newTransaction, Transaction oldTransaction) {
        return newTransaction.getComplytId() == null || newTransaction.getComplytId().equals(oldTransaction.getComplytId()) ?
                Mono.just(newTransaction) : Mono.empty();
    }

    @Override
    public Mono<Transaction> isNewDontHaveComplytId(Transaction newTransaction) {
        return newTransaction.getComplytId() == null ?
                Mono.just(newTransaction) : Mono.empty();
    }

    @Override
    public Transaction insertComplytIdToNew(Transaction newTransaction) {
        return (Transaction) newTransaction.withComplytId(UUID.randomUUID());
    }
}
