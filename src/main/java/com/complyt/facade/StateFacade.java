package com.complyt.facade;

import com.complyt.service.StateService;
import com.complyt.v1.model.StateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateFacade {

    @Autowired
    StateService stateService;

    public StateDto getState(String name) {
        return stateService.getState(name);
    }
}