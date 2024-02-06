package com.complyt.services;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.repositories.ClientTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class ClientTrackingServiceImpl implements ClientTrackingService {

    @NonNull
    private ClientTrackingRepository clientTrackingRepository;

    @Override
    public Mono<ClientTracking> save(ClientTracking clientTracking) {
        return clientTrackingRepository.save(clientTracking);
    }

    @Override
    public Mono<ClientTracking> findById(@NonNull String id) {
        return clientTrackingRepository.findById(id);
    }

    @Override
    public Mono<ClientTracking> getClientTracking() {
        return clientTrackingRepository.findClient();
    }

    public Mono<Nexus> getNexusInfo() {
        return getClientTracking()
                .map(ClientTracking::getNexus);
    }

    @Override
    public Flux<ClientTracking> findAll(int page, int size) {
        return clientTrackingRepository.findAll(page, size);
    }

    @Override
    public Flux<ClientTracking> getByName(String name) {
        return clientTrackingRepository.getByName(name);
    }

    @Override
    public Flux<ClientTracking> getByTenantId(String tenantId) {
        return clientTrackingRepository.getByTenantId(tenantId);
    }
}
