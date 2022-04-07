package com.complyt.services;

import com.complyt.domain.State;
import com.complyt.repositories.StateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {
    private StateRepository stateRepository;

    @Override
    public Mono<State> save(State state) {
        return stateRepository.save(state);
    }

    public Mono<State> findOneByName(String name) {
        return stateRepository.findOneByName(name);
    }

    @Override
    public Flux<State> findByName(String name) {
        return stateRepository.findByName(name);
    }

    @Override
    public Mono<State> findById(String id) {
        return stateRepository.findById(id);
    }

    @Override
    public Flux<State> findAll() {
        return stateRepository.findAll();
    }
}