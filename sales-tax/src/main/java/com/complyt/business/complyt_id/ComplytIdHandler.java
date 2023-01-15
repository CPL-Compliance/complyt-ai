package com.complyt.business.complyt_id;

import com.complyt.domain.customer.Customer;
import reactor.core.publisher.Mono;

public interface ComplytIdHandler<T> {
    public Mono<T> isComplytIdOfUpdatedEqualsToOld(T newEntity, T oldEntity);
    public Mono<T> isNewDontHaveComplytId(T newEntity);
    public T insertComplytIdToNew(T newEntity);
}
