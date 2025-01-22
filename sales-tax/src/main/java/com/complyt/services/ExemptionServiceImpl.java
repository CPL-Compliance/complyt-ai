package com.complyt.services;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.timestamps_injection.InternalTimestampsHandler;
import com.complyt.business.timestamps_injection.NewCustomerInternalTimestampsInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerStatus;
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

import java.util.Map;
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
    private InternalTimestampsHandler<Exemption> internalTimestampsHandler;

    @NonNull
    private StrategySelector exemptionListGeneratorStrategy;

    @Override
    public Mono<Exemption> findFullyExempted(@NonNull final Transaction transaction) {
        return exemptionRepository.findFullyExempted(transaction.getCustomerId(), transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getExternalTimestamps().getCreatedDate());
    }

    @Override
    public Mono<Exemption> findByComplytId(@NonNull UUID complytId) {
        return exemptionRepository.findByComplytId(complytId);
    }

    @Override
    public Mono<Boolean> isFullyExempted(@NonNull final Transaction transaction) {
        return findFullyExempted(transaction)
                .hasElement()
                // hasElement will return true in case of a full exemption found in the DB
                .flatMap(hasFullExemption -> {
                    String logStr = "Transaction with ComplytId: " + transaction.getComplytId() + " has full exemption returned: " + hasFullExemption;

                    return ContextLogger.observeCtx(logStr, log::info).then(Mono.just(hasFullExemption));
                });
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
    public Flux<Exemption> findAll(int offSet, int page, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return exemptionRepository.findAll(offSet, page);
    }

    @Override
    public Mono<Exemption> update(@NonNull final Exemption newExemption, @NonNull final Exemption existingExemption, @NonNull final UUID complytId) {
        return Mono.just(newExemption)
                .map(exemptionToUpdate -> internalTimestampsHandler.insertTimestampsToExisting(exemptionToUpdate, existingExemption))
                .map(createFunctionUpdateExemption(existingExemption))
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
        return Mono.just(exemption)
                .map(complytIdHandler::insertComplytIdToNew)
                .map(exemptionWithComplytId -> exemptionWithComplytId.withCountry(CountryToStandardizedCountry.standardize(exemptionWithComplytId.getCountry())))
                .map(internalTimestampsHandler::insertTimestampsToNew);
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
        return ((Flux<Exemption>) exemptionListGeneratorStrategy.select(exemptionWrapper).apply(exemptionWrapper))
                .flatMap(this::save);
    }

    @Override
    public Mono<Exemption> findByCountryStateAndCustomer(String country, String state, UUID customerId) {
        return exemptionRepository.findByCountryStateAndCustomer(country, state, customerId);
    }

    private Function<Exemption, Exemption> createFunctionUpdateExemption(Exemption exemptionInfo) {
        return newExemption ->
                new Exemption(
                        exemptionInfo.getComplytId(), exemptionInfo.getId(),
                        exemptionInfo.getTenantId(), newExemption.getCustomerId(),
                        newExemption.getCountry(), newExemption.getState(),
                        newExemption.getClassification(), newExemption.getValidationDates(),
                        newExemption.getInternalTimestamps(), newExemption.getStatus(),
                        newExemption.getCertificate(), newExemption.getExemptionType(),
                        newExemption.getExemptionStatus(), null
                );
    }

}