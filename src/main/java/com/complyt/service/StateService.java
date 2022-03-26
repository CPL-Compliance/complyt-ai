package com.complyt.service;

import com.complyt.domain.State;
import com.complyt.repository.StateRepository;
import com.complyt.v1.mapper.StateMapper;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StateService {
    private StateRepository stateRepository;

    public StateDto getStateByName(String name) {
        State state = stateRepository.findStateByName(name);
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        return stateDto;
    }
}