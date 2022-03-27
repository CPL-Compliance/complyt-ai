package com.complyt.facades;

import com.complyt.services.StateService;
import com.complyt.v1.model.StateDto;
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

    public StateDto findByName(String name) {
        return stateService.findByName(name);
    }
}