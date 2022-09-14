package com.complyt.facades;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.services.ExemptionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Component
public class ExemptionFacade {
    
    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    public Mono<Exemption> save(@NonNull Exemption exemption) {
        return exemptionService.save(exemption);
    }

    public Mono<Exemption> findById(@NonNull String id) {
        return exemptionService.findById(id);
    }

    public Flux<Exemption> findAll() {
        return exemptionService.findAll();
    }

    public Mono<Exemption> update(@NonNull Exemption exemption, @NonNull String id) {
        return exemptionService.update(exemption, id);
    }
}
