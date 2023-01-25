package com.complyt.business.complyt_id;

import reactor.core.publisher.Mono;

public interface ComplytIdHandler<T> {
    public Mono<T> checkComplytIdOfUpdatedEqualsToOld(T newEntity, T oldEntity);
    public Mono<T> checkNewDontHaveComplytId(T newEntity);
    public T insertComplytIdToNew(T newEntity);
}
