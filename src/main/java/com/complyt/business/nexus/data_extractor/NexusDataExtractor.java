package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;

public interface NexusDataExtractor<T, Order> {
    T extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule);
}
