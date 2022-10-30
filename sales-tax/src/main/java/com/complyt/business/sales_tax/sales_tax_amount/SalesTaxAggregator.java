package com.complyt.business.sales_tax.sales_tax_amount;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class SalesTaxAggregator {

    @NonNull
    List<ISalesTaxCalculator> calculators;

    public float aggregate() {
        Optional<Float> amount = calculators.stream().map(ISalesTaxCalculator::calculate).reduce(Float::sum);
        log.debug("Sales tax amount calculated : " + amount);

        return amount.get();
    }
}

