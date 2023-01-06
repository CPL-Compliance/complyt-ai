package com.complyt.business.complyt_id;

import com.complyt.domain.ComplytEntity;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class ComplytIdHandler<T extends ComplytEntity> {
    public Mono<T> isComplytIdOfUpdatedEqualsToOld(T newEntity, T oldEntity) {
        return newEntity.getComplytId() == null || newEntity.getComplytId().equals(oldEntity.getComplytId()) ?
                Mono.just(newEntity) : Mono.empty();
    }

    public Mono<T> isNewDontHaveComplytId(T newEntity) {
        return newEntity.getComplytId() == null ?
                Mono.just(newEntity) : Mono.empty();
    }

    public T insertComplytIdToNew(T newEntity) {
        return (T)newEntity.withComplytId(UUID.randomUUID());
    }
}
