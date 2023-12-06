package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.exemption.ExemptionListGenerator;
import com.complyt.business.sales_tax.checker.CustomerFullyExemptionChecker;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionStatus;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.ExemptionRepository;
import com.complyt.utils.observability.ContextLogger;
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

    @NonNull
    private ExemptionListGenerator exemptionListGenerator;

    @Override
    public Mono<Exemption> findByClientCustomerAndState(@NonNull final Transaction transaction) {
        return exemptionRepository.findByCustomerAndState(transaction.getCustomerId(), transaction.getShippingAddress().state());
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
        return checkExemptionNotHavingComplytId(exemption)
                .flatMap(this::injectDataToNewExemption)
                .flatMap(exemptionRepository::save);
    }

    @Deprecated
    @Override
    public Mono<Exemption> findById(@NonNull final String id) {
        return exemptionRepository.findById(id);
    }

    @Override
    public Flux<Exemption> findAll(int offSet, int limit) {
        return exemptionRepository.findAll(offSet, limit);
    }

    @Override
    public Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final Exemption originalExemption, @NonNull final UUID complytId) {
        return Mono.just(originalExemption)
                .map(createFunctionUpdateExemption(exemption))
                .flatMap(exemptionRepository::save);
    }

    @Override
    public Mono<Exemption> markAsCancelled(@NonNull final UUID complytId) {
        return exemptionRepository
                .findByComplytId(complytId)
                .flatMap(exemption -> {
                    String logStr = "Cancelling Exemption: " + exemption;

                    return ContextLogger.observeCtx(logStr, log::info)
                            .then(Mono.just(exemption.withExemptionStatus(ExemptionStatus.CANCELLED)));
                })
                .flatMap(exemptionRepository::save);
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

    @Override
    public Flux<Exemption> saveMany(@NonNull ExemptionWrapper exemptionWrapper) {
        return exemptionListGenerator.generate(exemptionWrapper)
                .flatMap(this::save);
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