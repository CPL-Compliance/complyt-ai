package com.complyt.services;

import com.complyt.business.sales_tax.checker.CustomerFullyExemptionCheck;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.repositories.ExemptionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
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
    public Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction) {
        return exemptionRepository.findByClientCustomerAndState(transaction);
    }

    @Override
    public Mono<Boolean> isFullyExempted(@NonNull Transaction transaction) {
        CustomerFullyExemptionCheck customerFullyExemptionCheck = new CustomerFullyExemptionCheck(transaction);

        return findByClientCustomerAndState(transaction)
                .map(customerFullyExemptionCheck::check)
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<Exemption> save(Exemption exemption) {
        return exemptionRepository.save(exemption);
    }

    @Override
    public Mono<Exemption> findById(@NonNull String id) {
        return exemptionRepository.findById(id);
    }

    @Override
    public Flux<Exemption> findAll() {
        return exemptionRepository.findAll();
    }

    @Override
    public Mono<Exemption> update(@NonNull Exemption exemption, @NonNull String id) {
        return exemptionRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("No Exemption with id " + id)))
                .map(createUpdateExemptionFunction(exemption))
                .flatMap(exemptionRepository::save);
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