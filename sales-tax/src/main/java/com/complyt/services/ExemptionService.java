package com.complyt.services;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.services.crud.CrudService;
import com.mongodb.client.result.DeleteResult;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ExemptionService extends CrudService<Exemption, String> {
    Mono<Exemption> findByClientCustomerAndState(@NonNull final Transaction transaction);

    Mono<Exemption> findByComplytId(@NonNull final UUID complytId);

    Mono<Boolean> isFullyExempted(@NonNull final Transaction transaction);

    Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final Exemption originalexemption, @NonNull final UUID complytId);

    Mono<DeleteResult> delete(final UUID complytId);

    Mono<Exemption> injectDataToNewExemption(@NonNull Exemption exemption);

    Mono<Exemption> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final Exemption modifiedExemption, @NonNull final Exemption originalExemption);

    Mono<Exemption> checkExemptionNotHavingComplytId(@NonNull final Exemption newExemption);
}
