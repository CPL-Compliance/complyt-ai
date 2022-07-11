package com.complyt.services;

import com.complyt.domain.nexus.NexusTracking;
import com.complyt.services.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface NexusTrackingService extends CrudService<NexusTracking,String> {

    Mono<NexusTracking> save(@NonNull NexusTracking nexusTracking);

    Mono<NexusTracking> findByState(@NonNull String state);

    Flux<NexusTracking> findAll();
}
