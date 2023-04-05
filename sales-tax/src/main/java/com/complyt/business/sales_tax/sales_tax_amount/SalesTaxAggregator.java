package com.complyt.business.sales_tax.sales_tax_amount;

import com.complyt.domain.Taxable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
@EqualsAndHashCode
@ToString
public class SalesTaxAggregator {

    public float aggregate(@NonNull List<Taxable> taxables) {
        Optional<Float> amount = taxables.stream().map(Taxable::calculateSalesTaxAmount).reduce(Float::sum);
        log.debug("Sales tax amount calculated : " + amount);

        return amount.get();
    }
}