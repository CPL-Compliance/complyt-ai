package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ExemptionService {
    Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction);

    Mono<Boolean> isFullyExempted(@NonNull Transaction transaction);
}
