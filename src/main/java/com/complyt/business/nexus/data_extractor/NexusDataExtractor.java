package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;

public interface NexusDataExtractor<T extends Number, Order> {
    T extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule);
}
