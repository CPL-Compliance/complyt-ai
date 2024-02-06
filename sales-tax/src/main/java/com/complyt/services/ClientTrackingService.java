package com.complyt.services;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.services.crud.CrudService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientTrackingService extends CrudService<ClientTracking, String> {
    Mono<Nexus> getNexusInfo();

    Mono<ClientTracking> getClientTracking();

    Flux<ClientTracking> getByName(String name);

    Mono<ClientTracking> getByTenantId(String tenantId);

    Mono<ClientTracking> saveByTenantId(ClientTracking clientTracking, String tenantId);

    Mono<ClientTracking> injectDataToExistingClientTracking(ClientTracking newClientTracking, ClientTracking originalClientTracking);

    Mono<ClientTracking> injectDataToNewClientTracking(ClientTracking clientTracking);

    Mono<ClientTracking> update(ClientTracking newClientTracking, ClientTracking originalClientTracking);
}
