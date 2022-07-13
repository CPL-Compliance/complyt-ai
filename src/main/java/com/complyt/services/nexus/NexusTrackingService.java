package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusTracking;
import com.complyt.services.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface NexusTrackingService extends CrudService<NexusTracking,String> {

    Mono<NexusTracking> save(@NonNull NexusTracking nexusTracking);

    Mono<NexusTracking> findByState(@NonNull String state);

    Flux<NexusTracking> findAll();

    Mono<NexusTracking> saveWithEconomicQualified(@NonNull NexusTracking nexusTracking);
}
