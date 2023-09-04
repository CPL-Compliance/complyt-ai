package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.sales_tax.checker.CustomerFullyExemptionChecker;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.repositories.ExemptionRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ExemptionServiceImpl implements ExemptionService {

    @NonNull
    private ExemptionRepository exemptionRepository;

    @NonNull
    private ComplytIdHandler<Exemption> complytIdHandler;

    @Override
    public Mono<Exemption> findByClientCustomerAndState(@NonNull final Transaction transaction) {
        return exemptionRepository.findByClientCustomerAndState(transaction);
    }

    @Override
    public Mono<Exemption> findByComplytId(@NonNull UUID complytId) {
        return exemptionRepository.findByComplytId(complytId);
    }

    @Override
    public Mono<Boolean> isFullyExempted(@NonNull final Transaction transaction) {
        CustomerFullyExemptionChecker customerFullyExemptionChecker = new CustomerFullyExemptionChecker(transaction);

        return findByClientCustomerAndState(transaction)
                .map(customerFullyExemptionChecker::check)
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Exemption> save(@NonNull final Exemption exemption) {
        return exemptionRepository.save(exemption);
    }

    @Deprecated
    @Override
    public Mono<Exemption> findById(@NonNull final String id) {
        return exemptionRepository.findById(id);
    }

    @Override
    public Flux<Exemption> findAll() {
        return exemptionRepository.findAll();
    }

    @Override
    public Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final Exemption originalExemption, @NonNull final UUID complytId) {
        return Mono.just(originalExemption)
                .map(createFunctionUpdateExemption(exemption))
                .flatMap(exemptionRepository::save);
    }

    @Override
    public Mono<DeleteResult> delete(@NonNull final UUID complytId) {
        return exemptionRepository.delete(complytId);
    }

    @Override
    public Mono<Exemption> injectDataToNewExemption(@NonNull Exemption exemption) {
        return Mono.just(exemption).map(complytIdHandler::insertComplytIdToNew);
    }

    @Override
    public Mono<Exemption> checkComplytIdOfModifiedEqualsToOriginal(@NonNull Exemption modifiedExemption, @NonNull Exemption originalExemption) {
        return complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(modifiedExemption, originalExemption);
    }

    @Override
    public Mono<Exemption> checkExemptionNotHavingComplytId(@NonNull Exemption newExemption) {
        return complytIdHandler.checkNewDontHaveComplytId(newExemption);
    }

    private Function<Exemption, Exemption> createFunctionUpdateExemption(Exemption exemption) {
        return exemptionInfo -> exemptionInfo
                .withCustomerId(exemption.getCustomerId())
                .withState(exemption.getState())
                .withClassification(exemption.getClassification())
                .withValidationDates(exemption.getValidationDates())
                .withInternalTimestamps(exemption.getInternalTimestamps())
                .withStatus(exemption.getStatus())
                .withCertificate(exemption.getCertificate())
                .withExemptionType(exemption.getExemptionType());
    }
}