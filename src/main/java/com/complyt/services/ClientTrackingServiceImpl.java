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
        return null;
    }

    @Override
    public Mono<ClientTracking> findOneByName(@NonNull String name) {
        return null;
    }

    @Override
    public Flux<ClientTracking> findByName(@NonNull String name) {
        return null;
    }

    @Override
    public Mono<ClientTracking> findById(@NonNull String id) {
        return null;
    }

    public Mono<Nexus> getNexusInfo() {
        return clientTrackingRepository.findClient().log()
                .map(ClientTracking::getNexus);
    }

    @Override
    public Flux<ClientTracking> findAll() {
        return null;
    }
}
