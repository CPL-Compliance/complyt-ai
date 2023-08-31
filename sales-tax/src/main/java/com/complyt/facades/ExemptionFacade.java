package com.complyt.facades;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.services.ExemptionService;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
public class ExemptionFacade {

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    public Mono<Exemption> save(@NonNull final Exemption exemption) {
        return exemptionService.save(exemption);
    }

    @Deprecated
    public Mono<Exemption> findById(@NonNull final String id) {
        return exemptionService.findById(id);
    }

    public Mono<Exemption> findByComplytId(@NonNull final UUID complytId) {
        return exemptionService.findByComplytId(complytId);
    }

    public Flux<Exemption> findAll() {
        return exemptionService.findAll();
    }

    public Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final UUID complytId) {
        return exemptionService.findByComplytId(complytId).flatMap(originalExemption ->
                        exemptionService.checkComplytIdOfModifiedEqualsToOriginal(exemption, originalExemption)
                                .flatMap(checkedExemption -> exemptionService.update(checkedExemption, originalExemption, complytId)))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }

    public Mono<DeleteResult> delete(@NonNull final UUID complytId) {
        return exemptionService.delete(complytId);
    }

    public Flux<Exemption> saveMany(@NonNull ExemptionWrapper exemptionWrapper) {
        return exemptionService.saveMany(exemptionWrapper);
    }
}
