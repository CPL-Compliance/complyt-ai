package com.complyt.services;

import com.complyt.domain.State;
import com.complyt.repositories.StateRepository;
import com.complyt.v1.mappers.StateMapper;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StateServiceImpl implements StateService {
    private StateRepository stateRepository;

    public StateDto findByName(String name) {
        State state = stateRepository.findStateByName(name);
        StateDto stateDto = StateMapper.INSTANCE.stateToStateDto(state);

        return stateDto;
    }
}