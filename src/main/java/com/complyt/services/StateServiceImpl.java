package com.complyt.services;

import com.complyt.domain.State;
import com.complyt.repositories.StateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {
    private StateRepository stateRepository;

    @Override
    public State save(State state) {
        return stateRepository.save(state);
    }

    public State findOneByName(String name) {
        return stateRepository.findOneByName(name);
    }

    @Override
    public List<State> findByName(String name) {
        return stateRepository.findByName(name);
    }

    @Override
    public State findById(String id) {
        return stateRepository.findById(id);
    }

    @Override
    public List<State> findAll() {
        return null;
    }
}