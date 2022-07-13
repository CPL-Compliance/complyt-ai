package com.complyt.services.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.repositories.NexusTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;

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
        return nexusTrackingRepository.save(nexusTracking);
    }

    @Override
    public Mono<NexusTracking> findByState(@NonNull String state) {
        return nexusTrackingRepository.findByState(state);
    }

    @Override
    public Flux<NexusTracking> findAll() {
        return null;
    }

    @Override
    public Mono<NexusTracking> saveWithEconomicQualified(@NonNull NexusTracking nexusTracking) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true,
                new Date());
        NexusTracking modifiedTracking = nexusTracking.withEconomicNexusTracker(newTracker);

        return save(modifiedTracking);
    }
}
