package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.repositories.NexusStateRuleRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Service
public class NexusStateRuleServiceImpl implements NexusStateRuleService {

    @NonNull
    private NexusStateRuleRepository nexusStateRuleRepository;

    @Override
    public Mono<NexusStateRule> save(NexusStateRule nexusStateRule) {
        return nexusStateRuleRepository.save(nexusStateRule);
    }

    @Override
    public Mono<NexusStateRule> findById(@NonNull String id) {
        return nexusStateRuleRepository.findById(id);
    }

    @Override
    public Flux<NexusStateRule> findAll(int page, int limit) {
        return nexusStateRuleRepository.findAll();
    }

    @Override
    public Mono<NexusStateRule> findByState(@NonNull String state) {
        return nexusStateRuleRepository.findByState(state);
    }
}
