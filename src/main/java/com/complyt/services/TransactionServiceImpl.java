package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @NonNull
    private TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> findByExternalId(@NonNull String externalId) {
        return transactionRepository.findByExternalId(externalId);
    }

    @Override
    public Mono<Transaction> upsert(@NonNull String externalId, @NonNull Transaction transaction) {
        return transactionRepository.findByExternalId(externalId)
                .switchIfEmpty(transactionRepository.save(transaction))
                .map(createUpdateTransactionFunction(transaction))
                .flatMap(transactionRepository::save);
    }

    public Mono<Transaction> update(@NonNull final String externalId, @NonNull final Transaction transaction) {
            return transactionRepository.findByExternalId(transaction.getExternalId())
                .switchIfEmpty(Mono.error(new NotFoundException("No Transaction with externalId" + externalId)))
                .map(createUpdateTransactionFunction(transaction))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> markAsCancelled(String externalId) {
        return transactionRepository
                .findByExternalId(externalId)
                .map(transaction -> transaction.withTransactionStatus(TransactionStatus.CANCELLED))
                .flatMap(transactionRepository::save);
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.find();
    }

    @Override
    public void save(@NonNull List<ObjectId> transactions) {
        throw new UnsupportedOperationException("save isn't implemented yet");
    }

    @Override
    public Flux<Transaction> findByName(String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<Transaction> findOneByName(String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    private Function<Transaction, Transaction> createUpdateTransactionFunction(@NonNull final Transaction transaction) {
        return transactionInfo -> transactionInfo.withExternalId(transaction.getExternalId())
                .withBillingAddress(transaction.getBillingAddress())
                .withShippingAddress(transaction.getShippingAddress())
                .withCustomerId(transaction.getCustomerId())
                .withItems(transaction.getItems())
                .withTransactionStatus(transaction.getTransactionStatus())
                .withSalesTax(transaction.getSalesTax());
    }
}
