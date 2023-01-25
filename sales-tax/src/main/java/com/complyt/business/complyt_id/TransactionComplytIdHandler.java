package com.complyt.business.complyt_id;

import com.complyt.domain.Transaction;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class TransactionComplytIdHandler implements ComplytIdHandler<Transaction> {
    @Override
    public Mono<Transaction> checkComplytIdOfUpdatedEqualsToOld(Transaction newTransaction, Transaction oldTransaction) {
        return newTransaction.getComplytId() == null || newTransaction.getComplytId().equals(oldTransaction.getComplytId()) ?
                Mono.just(newTransaction) : Mono.error(new NotFoundException("complyt ids of modified and original transactions are not equal"));
    }

    @Override
    public Mono<Transaction> checkNewDontHaveComplytId(Transaction newTransaction) {
        return newTransaction.getComplytId() == null ?
                Mono.just(newTransaction) : Mono.error(new NotFoundException("cannot insert new transaction with complyt id"));
    }

    @Override
    public Transaction insertComplytIdToNew(Transaction newTransaction) {
        return (Transaction) newTransaction.withComplytId(UUID.randomUUID());
    }
}
