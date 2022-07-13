package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.repositories.NexusStateRuleRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class NexusStateRuleServiceImpl implements NexusStateRuleService {

    @NonNull
    private NexusStateRuleRepository nexusStateRuleRepository;

    @Override
    public Mono<NexusStateRule> save(NexusStateRule nexusStateRule) {
        return null;
    }

    @Override
    public Mono<NexusStateRule> findOneByName(@NonNull String name) {
        return null;
    }

    @Override
    public Flux<NexusStateRule> findByName(@NonNull String name) {
        return null;
    }

    @Override
    public Mono<NexusStateRule> findById(@NonNull String id) {
        return null;
    }

    @Override
    public Flux<NexusStateRule> findAll() {
        return null;
    }

    @Override
    public Mono<NexusStateRule> findByState(@NonNull String state) {
        return nexusStateRuleRepository.findByState(state);
    }
}
