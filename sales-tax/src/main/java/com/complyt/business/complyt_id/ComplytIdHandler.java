package com.complyt.business.complyt_id;

import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ComplytIdHandler<T extends ComplytIdProperty> {
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
