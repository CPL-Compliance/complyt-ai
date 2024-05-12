package com.complyt.business.strategy.exemption_list_generation;

import com.complyt.business.strategy.FunctionSelectorByExemptionCountryStrategy;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class ExemptionListGeneratorStrategy extends FunctionSelectorByExemptionCountryStrategy {

    @NonNull
    ExemptionListGenerator usaAddressExemptionListGenerator;

    @NonNull
    ExemptionListGenerator nonUsaAddressExemptionListGenerator;

    @Override
    protected Function<ExemptionWrapper, Flux<Exemption>> getFunctionForUsaOption(ExemptionWrapper exemptionWrapper) {
        return (exemptionWrapperToBeGeneratedFrom) -> usaAddressExemptionListGenerator.generate(exemptionWrapper).apply(exemptionWrapperToBeGeneratedFrom);
    }

    @Override
    protected Function<ExemptionWrapper, Flux<Exemption>> getFunctionForNonUsaOption(ExemptionWrapper exemptionWrapper) {
        return (exemptionWrapperToBeGeneratedFrom) -> nonUsaAddressExemptionListGenerator.generate(exemptionWrapper).apply(exemptionWrapperToBeGeneratedFrom);
    }
}
