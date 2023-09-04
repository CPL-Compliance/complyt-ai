package com.complyt.business.exemption;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ExemptionListGenerator implements ListGenerator<ExemptionWrapper> {

    public Flux<Exemption> generate(@NonNull ExemptionWrapper exemptionWrapper) {
        log.debug("Creating exemptions list based on exemption: " + exemptionWrapper.exemption());
        log.debug("With states: " + exemptionWrapper.states());
        List<Exemption> exemptionList = new ArrayList<>();
        for (State state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return Flux.fromIterable(exemptionList);
    }

}