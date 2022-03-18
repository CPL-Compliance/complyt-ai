package com.complyt.service;

import com.complyt.domain.State;
import com.complyt.repository.StateRepository;
import com.complyt.v1.mapper.StateMapper;
import com.complyt.v1.model.StateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateService {
    @Autowired
    private StateRepository stateRepository;

    public StateDto getState(String name) {
        State state = stateRepository.findByName(name);
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        return stateDto;
    }
}