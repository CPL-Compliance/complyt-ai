package com.complyt.services;

import com.complyt.business.sales_tax.checker.CustomerFullyExemptionCheck;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.repositories.ExemptionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class ExemptionServiceImpl implements ExemptionService {

    @NonNull
    private ExemptionRepository exemptionRepository;

    @Override
    public Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction) {
        return exemptionRepository.findByClientCustomerAndState(transaction).log();
    }

    @Override
    public Mono<Boolean> isFullyExempted(@NonNull Transaction transaction) {
        CustomerFullyExemptionCheck customerFullyExemptionCheck = new CustomerFullyExemptionCheck(transaction);

        return findByClientCustomerAndState(transaction)
                .map(customerFullyExemptionCheck::check)
                .switchIfEmpty(Mono.just(false));
    }
}
