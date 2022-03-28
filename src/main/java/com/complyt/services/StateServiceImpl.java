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

    @Override
    public StateDto save(StateDto stateDto) {
        State state = StateMapper.INSTANCE.stateDtoToState(stateDto);
        State returnedState = stateRepository.save(state);

        return StateMapper.INSTANCE.stateToStateDto(returnedState);
    }

    public StateDto findByName(String name) {
        State state = stateRepository.findByName(name);

        return StateMapper.INSTANCE.stateToStateDto(state);
    }

    @Override
    public StateDto findById(String id) {
        State state = stateRepository.findById(id);

        return StateMapper.INSTANCE.stateToStateDto(state);
    }
}