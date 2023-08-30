package com.complyt.business.exemption;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExemptionListBuilder {

    public Flux<Exemption> build(@NonNull ExemptionWrapper exemptionWrapper) {
        List<Exemption> exemptionList = new ArrayList<>();
        for (State state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return Flux.fromIterable(exemptionList);
    }
}
