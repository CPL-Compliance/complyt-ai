package com.complyt.business.complyt_id;

import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
public class ComplytIdHandler<T extends ComplytIdProperty> {

    public Mono<T> checkComplytIdOfUpdatedEqualsToOld(@NonNull T newEntity, @NonNull T oldEntity) {
        return newEntity.getComplytId() == null || newEntity.getComplytId().equals(oldEntity.getComplytId()) ?
                Mono.just(newEntity) : Mono.error(new ConflictedDataApiException(List.of(DtoErrorMessages.COMPLYT_ID_CANNOT_BE_UPDATED_ERROR)));
    }

    public Mono<T> checkNewDontHaveComplytId(@NonNull T newEntity) {
        return newEntity.getComplytId() == null ?
                Mono.just(newEntity) : Mono.error(new ConflictedDataApiException(List.of(DtoErrorMessages.COMPLYT_ID_IN_A_NEW_RECORD_ERROR)));
    }

    public T insertComplytIdToNew(@NonNull T newEntity) {
        return (T) newEntity.withComplytId(UUID.randomUUID());
    }
}
