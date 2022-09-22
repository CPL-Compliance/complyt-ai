package com.complyt.services;

import com.complyt.business.sales_tax.checker.CustomerFullyExemptionCheck;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.repositories.ExemptionRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ExemptionServiceImpl implements ExemptionService {

    @NonNull
    private ExemptionRepository exemptionRepository;

    @Override
    public Mono<Exemption> findByClientCustomerAndState(@NonNull final Transaction transaction) {
        return exemptionRepository.findByClientCustomerAndState(transaction);
    }

    @Override
    public Mono<Boolean> isFullyExempted(@NonNull final Transaction transaction) {
        CustomerFullyExemptionCheck customerFullyExemptionCheck = new CustomerFullyExemptionCheck(transaction);

        return findByClientCustomerAndState(transaction)
                .map(customerFullyExemptionCheck::check)
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Exemption> save(@NonNull final Exemption exemption) {
        return exemptionRepository.save(exemption);
    }

    @Override
    public Mono<Exemption> findById(@NonNull final String id) {
        return exemptionRepository.findById(id);
    }

    @Override
    public Flux<Exemption> findAll() {
        return exemptionRepository.findAll();
    }

    @Override
    public Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final String id) {
        return exemptionRepository.findById(id)
                .map(createUpdateExemptionFunction(exemption))
                .flatMap(exemptionRepository::save);
    }

    @Override
    public Mono<DeleteResult> delete(@NonNull final String id) {
        return exemptionRepository.delete(id);
    }

    private Function<Exemption, Exemption> createUpdateExemptionFunction(Exemption exemption) {
        return exemptionInfo -> exemptionInfo
                .withCustomerId(exemption.getCustomerId())
                .withState(exemption.getState())
                .withClassification(exemption.getClassification())
                .withValidationDates(exemption.getValidationDates())
                .withInternalTimeStamps(exemption.getInternalTimeStamps())
                .withStatus(exemption.getStatus())
                .withCertificate(exemption.getCertificate())
                .withExemptionType(exemption.getExemptionType());
    }
}