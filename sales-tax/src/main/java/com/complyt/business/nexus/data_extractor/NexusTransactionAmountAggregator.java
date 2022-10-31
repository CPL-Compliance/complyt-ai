package com.complyt.business.nexus.data_extractor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Getter
@AllArgsConstructor
public class NexusTransactionAmountAggregator {

    @NonNull
    private List<IAmountExtractor> amountExtractors;

    public float aggregate() {
        Optional<Float> amount = amountExtractors.stream().map(IAmountExtractor::extract).reduce(Float::sum);

        return amount.get();
    }
}
