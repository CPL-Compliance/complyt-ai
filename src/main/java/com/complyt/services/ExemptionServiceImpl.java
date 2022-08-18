package com.complyt.services;

import com.complyt.business.sales_tax.CustomerFullyExemptionCheck;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.repositories.ExemptionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class ExemptionServiceImpl implements ExemptionService {

    @NonNull
    private ExemptionRepository exemptionRepository;

    @NonNull
    private CustomerFullyExemptionCheck customerFullyExemptionCheck;

    @Override
    public Mono<Exemption> save(Exemption exemption) {
        return null;
    }

    @Override
    public Mono<Exemption> findById(@NonNull String id) {
        return null;
    }

    @Override
    public Flux<Exemption> findAll() {
        return null;
    }

    @Override
    public Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction) {
        return exemptionRepository.findByClientCustomerAndState(transaction).log();
    }

    public Mono<Boolean> isFullyExempted(@NonNull Transaction transaction) {
        return findByClientCustomerAndState(transaction)
                .map(exemption -> customerFullyExemptionCheck.isFullyExempted(transaction, exemption))
                .switchIfEmpty(Mono.just(false));
    }
}
