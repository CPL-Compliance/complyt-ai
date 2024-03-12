package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;

@EqualsAndHashCode
@AllArgsConstructor
public class TaxableCollectionAmountExtractor {

    @NonNull
    private QualificationChecker qualificationChecker;

    @NonNull
    private Collection<Taxable> taxables;

    @NonNull
    private NexusStateRule nexusStateRule;

    public Mono<BigDecimal> extract() {
        return Mono.just(taxables.stream()
                        .filter(taxable -> qualificationChecker.isQualified(taxable, nexusStateRule))
                        .toList())
                .flatMap(filteredTaxables -> filteredTaxables.isEmpty()
                        ? Mono.empty()
                        : Flux.fromIterable(filteredTaxables)
                        .reduce(BigDecimal.ZERO, (amountSum, taxable) -> amountSum.add(taxable.getCalculatedTotal())));
    }
}
