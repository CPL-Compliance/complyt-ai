package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;

public interface NexusDataExtractor<T extends Number, Transaction> {
    T extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule);
}
