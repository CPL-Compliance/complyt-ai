package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;

public interface NexusDataExtractor<T, Obj> {
    T extract(@NonNull Obj obj, @NonNull NexusStateRule nexusStateRule);
}
