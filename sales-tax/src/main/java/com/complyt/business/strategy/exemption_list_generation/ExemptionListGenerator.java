package com.complyt.business.strategy.exemption_list_generation;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public interface ExemptionListGenerator {
    Function<ExemptionWrapper, Flux<Exemption>> generate(ExemptionWrapper exemptionWrapper);
}
