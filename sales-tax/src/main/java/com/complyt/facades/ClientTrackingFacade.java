package com.complyt.facades;

import com.complyt.domain.ClientTracking;
import com.complyt.services.ClientTrackingService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Service
public class ClientTrackingFacade {

    @NonNull
    @Qualifier("ClientTrackingServiceImpl")
    private ClientTrackingService clientTrackingService;

    public Flux<ClientTracking> getAll(int page, int size) {
        return clientTrackingService.findAll(page,size);
    }

    public Flux<ClientTracking> getByTenantId(String tenantId) {
        return clientTrackingService.getByTenantId(tenantId);
    }

    public Flux<ClientTracking> getByName(String name) {
        return clientTrackingService.getByName(name);
    }

    public Mono<ClientTracking> save(ClientTracking clientTracking) {
        return clientTrackingService.save(clientTracking);
    }
}
