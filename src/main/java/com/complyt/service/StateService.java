package com.complyt.service;

import com.complyt.model.State;
import com.complyt.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService {

    @Autowired
    StateRepository stateRepository;

    public State getState(String name) {
        return stateRepository.findByName(name);
    }
}