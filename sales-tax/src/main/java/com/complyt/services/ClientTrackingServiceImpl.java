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

@AllArgsConstructor
@Slf4j
@Service
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
    public Flux<ClientTracking> findAll(int offSet, int limit) {
        return clientTrackingRepository.findAll();
    }
}
