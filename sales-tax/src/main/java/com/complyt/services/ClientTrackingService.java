package com.complyt.services;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientTrackingService extends CrudService<ClientTracking, String> {
    Mono<Nexus> getNexusInfo();

    Mono<ClientTracking> getClientTracking();

    Flux<ClientTracking> getByName(@NonNull String name);

    Mono<ClientTracking> getByTenantId(@NonNull String tenantId);

    Mono<ClientTracking> saveByTenantId(@NonNull ClientTracking clientTracking, @NonNull String tenantId);

    Mono<ClientTracking> injectDataToExistingClientTracking(@NonNull ClientTracking newClientTracking, @NonNull ClientTracking originalClientTracking);

    Mono<ClientTracking> injectDataToNewClientTracking(@NonNull ClientTracking clientTracking);

    Mono<ClientTracking> update(@NonNull ClientTracking newClientTracking, @NonNull ClientTracking originalClientTracking);
}
