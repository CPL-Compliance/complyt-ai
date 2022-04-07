package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.services.StateService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class StateFacade {

    @Qualifier("stateServiceImpl")
    @NonNull
    private StateService stateService;

    public Mono<State> findByName(String name) {
        return stateService.findOneByName(name);
    }
}