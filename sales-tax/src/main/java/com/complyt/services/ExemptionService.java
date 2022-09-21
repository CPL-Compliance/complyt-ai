package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.services.crud.CrudService;
import com.mongodb.client.result.DeleteResult;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ExemptionService extends CrudService<Exemption, String> {
    Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction);

    Mono<Boolean> isFullyExempted(@NonNull Transaction transaction);

    Mono<Exemption> update(@NonNull Exemption exemption, @NonNull String id);

    Mono<DeleteResult> delete(String id);
}
