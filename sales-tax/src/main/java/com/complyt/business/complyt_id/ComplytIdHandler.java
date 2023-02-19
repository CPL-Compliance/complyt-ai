package com.complyt.business.complyt_id;

import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ComplytIdHandler<T extends ComplytIdProperty> {
    public Mono<T> checkComplytIdOfUpdatedEqualsToOld(@NonNull T newT, @NonNull T oldT) {
        return newT.getComplytId() == null || newT.getComplytId().equals(oldT.getComplytId()) ?
                Mono.just(newT) : Mono.error(new ConflictedDataApiException());
    }

    public Mono<T> checkNewDontHaveComplytId(@NonNull T newT) {
        return newT.getComplytId() == null ?
                Mono.just(newT) : Mono.error(new ConflictedDataApiException());
    }

    public T insertComplytIdToNew(@NonNull T newT) {
        return (T)newT.withComplytId(UUID.randomUUID());
    }
}
