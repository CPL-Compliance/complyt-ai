package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface NexusDataExtractor<T extends Number, Transaction> {
    Mono<T> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule);
}
