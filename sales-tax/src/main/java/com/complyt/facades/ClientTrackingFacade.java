package com.complyt.facades;

import com.complyt.domain.ClientTracking;
import com.complyt.services.ClientTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ClientTrackingFacade {

    @NonNull
    private ClientTrackingService clientTrackingService;

    public Flux<ClientTracking> getAll(int page, int size) {
        return clientTrackingService.findAll(page, size);
    }

    public Mono<ClientTracking> getByTenantId(String tenantId) {
        return clientTrackingService.getByTenantId(tenantId);
    }

    public Flux<ClientTracking> getByName(String name) {
        return clientTrackingService.getByName(name);
    }

    public Mono<ClientTracking> saveClientTracking(ClientTracking clientTracking, String tenantId) {
        return clientTrackingService.injectDataToNewClientTracking(clientTracking)
                .flatMap(updatedClientTracking -> clientTrackingService.saveByTenantId(updatedClientTracking, tenantId));
    }

    public Mono<ClientTracking> updateIfModified(ClientTracking newClientTracking, ClientTracking originalClientTracking, String tenantId) {
        return originalClientTracking.equals(newClientTracking) ?
                Mono.just(newClientTracking) :
                clientTrackingService.update(newClientTracking, originalClientTracking)
                        .flatMap(updatedClientTracking -> clientTrackingService.saveByTenantId(updatedClientTracking, tenantId));
    }
}
