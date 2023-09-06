package com.complyt.business.exemption;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.utils.observability.ContextLogger;
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
        List<Exemption> exemptionList = new ArrayList<>();
        for (State state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return ContextLogger.observeCtx("Created Exemptions list: " + exemptionList, log::info)
                .thenMany(Flux.fromIterable(exemptionList));
    }
}