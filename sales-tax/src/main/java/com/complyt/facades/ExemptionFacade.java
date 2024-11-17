package com.complyt.facades;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.services.ExemptionService;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Component
public class ExemptionFacade {

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    @Deprecated
    public Mono<Exemption> findById(@NonNull final String id) {
        return exemptionService.findById(id);
    }

    public Mono<Exemption> findByComplytId(@NonNull final UUID complytId) {
        return exemptionService.findByComplytId(complytId);
    }

    public Flux<Exemption> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return exemptionService.findAll(page, size, filterMap, sortOrder, sortBy);
    }

    public Mono<Exemption> update(@NonNull final Exemption exemption, @NonNull final UUID complytId) {
        return exemptionService.findByComplytId(complytId).flatMap(originalExemption ->
                        exemptionService.checkComplytIdOfModifiedEqualsToOriginal(exemption, originalExemption)
                                .flatMap(checkedExemption -> exemptionService.update(checkedExemption, originalExemption, complytId)))
                .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in ExemptionFacade.update for complytId " + complytId, log::error)
                        .then(Mono.error(new ObjectNotFoundApiException())));
    }

    public Mono<Exemption> markAsCancelled(@NonNull final UUID complytId) {
        return exemptionService.markAsCancelled(complytId);
    }

    public Flux<Exemption> save(@NonNull ExemptionWrapper exemptionWrapper) {
        return exemptionService.saveMany(exemptionWrapper);
    }

}