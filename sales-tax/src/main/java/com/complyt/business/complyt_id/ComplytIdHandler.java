package com.complyt.business.complyt_id;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.fields.ComplytIdFieldDomain;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class ComplytIdHandler<T extends ComplytIdFieldDomain> {
    public Mono<T> checkComplytIdOfUpdatedEqualsToOld(T newT, T oldT) {
        return newT.getComplytId() == null || newT.getComplytId().equals(oldT.getComplytId()) ?
                Mono.just(newT) : Mono.error(new ConflictedDataApiException());
    }

    public Mono<T> checkNewDontHaveComplytId(T newT) {
        return newT.getComplytId() == null ?
                Mono.just(newT) : Mono.error(new ConflictedDataApiException());
    }

    public T insertComplytIdToNew(T newT) {
        return (T)newT.withComplytId(UUID.randomUUID());
    }
}
