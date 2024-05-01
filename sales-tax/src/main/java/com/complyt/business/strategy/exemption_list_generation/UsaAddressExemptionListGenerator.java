package com.complyt.business.strategy.exemption_list_generation;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class UsaAddressExemptionListGenerator implements ExemptionListGenerator {

    @Override
    public Function<ExemptionWrapper, Flux<Exemption>> generate(ExemptionWrapper exemptionWrapper) {
        return exemptionWrapperToBeGeneratedFrom -> {
            List<Exemption> exemptionList = new ArrayList<>();
            for (State state : exemptionWrapper.states()) {
                exemptionList.add(exemptionWrapper.exemption().withState(state));
            }

            return ContextLogger.observeCtx("Created Exemptions list: " + exemptionList, log::info)
                    .thenMany(Flux.fromIterable(exemptionList));
        };
    }
}