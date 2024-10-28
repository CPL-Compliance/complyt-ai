package com.complyt.facades;

import com.complyt.domain.ClientTracking;
import com.complyt.services.ClientTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
public class ClientTrackingFacade {

    @NonNull
    private ClientTrackingService clientTrackingService;

    public Flux<ClientTracking> getAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return clientTrackingService.findAll(page, size, filterMap, sortOrder, sortBy);
    }

    public Mono<ClientTracking> getByTenantId(@NonNull String tenantId) {
        return clientTrackingService.getByTenantId(tenantId);
    }

    public Flux<ClientTracking> getByName(@NonNull String name) {
        return clientTrackingService.getByName(name);
    }

    public Mono<ClientTracking> saveClientTracking(@NonNull ClientTracking clientTracking, @NonNull String tenantId) {
        return clientTrackingService.injectDataToNewClientTracking(clientTracking)
                .flatMap(updatedClientTracking -> clientTrackingService.saveByTenantId(updatedClientTracking, tenantId));
    }

    public Mono<ClientTracking> updateIfModified(@NonNull ClientTracking newClientTracking, @NonNull ClientTracking originalClientTracking, @NonNull String tenantId) {
        return originalClientTracking.equals(newClientTracking) ?
                Mono.just(newClientTracking) :
                clientTrackingService.update(newClientTracking, originalClientTracking)
                        .flatMap(updatedClientTracking -> clientTrackingService.saveByTenantId(updatedClientTracking, tenantId));
    }
}
