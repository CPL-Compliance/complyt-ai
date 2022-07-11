package com.complyt.services;

import com.complyt.domain.nexus.NexusTracking;
import com.complyt.repositories.NexusTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Service
public class NexusTrackingServiceImpl implements NexusTrackingService {

    @NonNull
    private NexusTrackingRepository nexusTrackingRepository;

    @Override
    public Mono<NexusTracking> findOneByName(@NonNull String name) {
        return null;
    }

    @Override
    public Flux<NexusTracking> findByName(@NonNull String name) {
        return null;
    }

    @Override
    public Mono<NexusTracking> findById(@NonNull String s) {
        return null;
    }

    @Override
    public Mono<NexusTracking> save(@NonNull NexusTracking nexusTracking) {
        return null;
    }

    @Override
    public Mono<NexusTracking> findByState(@NonNull String state) {
        return nexusTrackingRepository.findByState(state);
    }

    @Override
    public Flux<NexusTracking> findAll() {
        return null;
    }
}
