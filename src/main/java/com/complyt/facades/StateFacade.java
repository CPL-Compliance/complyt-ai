package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.services.StateService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StateFacade {

    @Qualifier("stateServiceImpl")
    @NonNull
    private StateService stateService;

    public State findByName(String name) {
        return stateService.findOneByName(name);
    }
}