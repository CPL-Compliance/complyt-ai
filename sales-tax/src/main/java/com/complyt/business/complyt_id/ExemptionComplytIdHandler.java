package com.complyt.business.complyt_id;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class ExemptionComplytIdHandler implements ComplytIdHandler<Exemption> {
    @Override
    public Mono<Exemption> checkComplytIdOfUpdatedEqualsToOld(Exemption newExemption, Exemption oldExemption) {
        return newExemption.getComplytId() == null || newExemption.getComplytId().equals(oldExemption.getComplytId()) ?
                Mono.just(newExemption) : Mono.error(new ConflictedDataApiException());
    }

    @Override
    public Mono<Exemption> checkNewDontHaveComplytId(Exemption newExemption) {
        return newExemption.getComplytId() == null ?
                Mono.just(newExemption) : Mono.error(new ConflictedDataApiException());
    }

    @Override
    public Exemption insertComplytIdToNew(Exemption newExemption) {
        return newExemption.withComplytId(UUID.randomUUID());
    }
}
