package com.complyt.services;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.services.crud.CrudService;
import reactor.core.publisher.Mono;

public interface ClientTrackingService extends CrudService<ClientTracking, String> {
    Mono<Nexus> getNexusInfo();

    Mono<ClientTracking> getClientTracking();
}
